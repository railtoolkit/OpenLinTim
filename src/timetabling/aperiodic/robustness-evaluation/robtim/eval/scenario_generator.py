#!/usr/bin/env python3
# -*- coding: utf-8 -*-
__all__ = [
        "ScenarioGenerator",
        "ScenarioScheduler", 
        "ConfigurableScenarioGenerator", 
        "ScAlbertU1",
        "ScAlbertU2",
        "DistributionScenarioGenerator",
        "PoissonDistScenarioGenerator",
        "NormalDistScenarioGenerator",
        "TreeOnTrackScenarioGenerator"
    ]

import itertools, numpy, functools
from robtim import Dataset, LinTimCSVWriter

class ScenarioGenerator:
    """
    Abstract super class of all scenario generators. A scenario generator models 
    a set of delay scenarios.
    
    A scenario generator uses a python generator, implemented in :func:`scenarios`
    to yield delay scenarios. Be aware that a scenario generator may yield infinetly
    many delay scenarios. When using :class:`RobustnessEvaluator` you have to take care
    of the termination yourself. Use for example :class:`ScenarioScheduler`.
    """
    def initialize(self, dataset : Dataset):
        """
        Initializes the generator. This is where configuration changes for LinTim
        components should be applied.
        
        :param dataset: dataset which delays should be generated on
        """
        pass
    def scenarios(self, dataset : Dataset):
        """
        This function has to be a python generator which generates a new delay
        scenario and yields a dict of its properties as long as more delays are 
        available.
        
        The dict of properties should correspond to the parameters of LinTim 
        component dm-delays.
        
        :param dataset: dataset which delays should be generated on
        """
        pass
    def reset(self, dataset : Dataset):
        """
        Deletes all generated delays from the dataset. This affects the following files
        in the dataset's directory:
            
        - delay-management/Delays-Activities.giv
        - delay-management/Delays-Events.giv
        
        :param dataset: dataset which delays should be generated on
        """
        dataset.delete("delay-management/Delays-Activities.giv")
        dataset.delete("delay-management/Delays-Events.giv")
    
class ScenarioScheduler(ScenarioGenerator):
    """
    Schedules different scenario generators for successive execution. In this way
    it ensures that the evaluation terminates.
    
    :example: ScenarioScheduler(ScAlbertU1(4), iterations=[5]) models the scenario set constisting of 5 scenarios generated by ScAlbertU1 with parameter 4.
    
    :param args: one or several scenario generators
    :param iterations: list of integers. The i-th value states how many scenarios from the i-th scenario generator in args should be taken
    """
    def __init__(self, *args, iterations : list = []):
        self.generators = args
        self.iterations = iterations
    def initialize(self, dataset : Dataset):
        pass
    def scenarios(self, dataset : Dataset):
        for sc, it in zip(self.generators, itertools.chain(self.iterations, itertools.repeat(None))):
            sc.initialize(dataset)
            for scenario in itertools.islice(sc.scenarios(dataset), it):
                yield scenario

class ConfigurableScenarioGenerator(ScenarioGenerator):
    """
    Generates delay scenarios using LinTim's dm-delays component. The given configuration    
    is used.
    
    Be aware that this generator will generate infinetly many delays. Use :class:`ScenarioScheduler`
    to guarantee that your evaluation terminates.
    
    :param config: configuration for LinTim's dm-delays
    """
    def __init__(self, config):
        self.config = config
    def initialize(self, dataset : Dataset):
        dataset.applyConfig(self.config)
    def scenarios(self, dataset : Dataset):
        while True:
            dataset.make("dm-delays")
            yield dict(self.config)

class ScAlbertU1(ConfigurableScenarioGenerator):
    """
    Scenario generator for the scenario set U_1 defined in Bachelor thesis by Albert
    
    :param s: max source delay
    :param smin: (optional) min source delays
    """
    def __init__(self, s, smin = 0):
        super().__init__({
                "delays_generator": "uniform_distribution",
                "delays_events": False,
                "delays_activities": True,
                "delays_append" : False,
                "delays_absolute_numbers" : False,
                "delays_min_delay": smin,
                "delays_max_delay": s,
                "delays_count_is_absolute" : False,
                "delays_count" : 100
            })

class ScAlbertU2(ConfigurableScenarioGenerator):
    """
    Scenario generator for the scenario set U_1 defined in Bachelor thesis by Albert
    
    :param s: max source delay
    :param k: max amount of activities delayed
    :param smin: (optional) min source delays
    """
    def __init__(self, s, k, smin = 0):
        super().__init__({
                "delays_generator": "uniform_distribution",
                "delays_events": False,
                "delays_activities": True,
                "delays_append" : False,
                "delays_absolute_numbers" : False,
                "delays_min_delay": smin,
                "delays_max_delay": s,
                "delays_count_is_absolute" : True,
                "delays_count" : k
            })

class DistributionScenarioGenerator(ScenarioGenerator):
    """
    Generates delay scenarios with a custom distribution
    
    In addition to the regular fields described in :class:`ScenarioGenerator`
    this generator yields for every scenario the following values:
        
        - `delays_total` sum of all delays on activities and events in seconds
        - `delays_total_weighted` sum of all delays weighted by the amount
          of passengers on the activities / events in seconds
        - `delays_total_passengers` total amount of passengers affected by the 
          delays, i.e. number of passengers on activities / events with delays
        - `delays_average` = `delays_total` / `delays_total_passengers`
        - `delays_average_weighted` = `delays_total_weighted` / `delays_total_passengers`
    
    :param randomizer: function int -> list[int] which returns a given amount of random delays
    :param count: number/percantage of events/activites which shall be delayed
    :param count_abs: whether count is absolute (True) or relative (False)
    :param events: whether events shall be delayed
    :param activities: whether activities shall be delays
    :param info: info about the distribution for statistical evaluation
    """
    def __init__(self, randomizer, count : int = 10, count_abs : bool = True, events : bool = False, activities : bool = True, info : dict = None):
        self.randomizer = randomizer
        self.count = count
        self.count_abs = count_abs
        self.events = events
        self.activities = activities
        self.info = info
    def initialize(self, dataset):
        self.__act_ids = dataset.readCSV("delay-management/Activities-expanded.giv", columns=[0,2,6])
        self.__act_ids = list(filter(lambda x: x[1] == "drive", self.__act_ids))
        self.__evt_ids = dataset.readCSV("delay-management/Events-expanded.giv", columns=[0,4])
    def scenarios(self, dataset : Dataset):
        while True:
            self.reset(dataset)
            
            totalDelay = 0
            totalDelayWeighted = 0
            totalPassengers = 0
            
            num_act = self.count if self.activities else 0
            num_evt = self.count if self.events else 0
            if self.count_abs and self.events and self.activities:
                    num_act = numpy.random.rand(0, self.count)
                    num_evt = self.count - num_act
            if num_act > 0:
                activities = list(self.__act_ids)
                if not self.count_abs:
                    num_act = len(activities) * self.count // 100
                delays = self.randomizer(num_act)[:num_act]
                numpy.random.shuffle(activities)
                with open(dataset.realPath("delay-management/Delays-Activities.giv"), "wt") as f:
                    writer = LinTimCSVWriter(f)
                    writer.write("# RobTim random delays")
                    for (act, delay) in zip(activities, delays):
                        if int(delay) != 0:
                            writer.write([act[0], int(delay)])
                            totalDelay += int(delay)
                            totalDelayWeighted += int(delay) * act[2]
                            totalPassengers += act[2]
            if num_evt > 0:
                events = list(self.__evt_ids)
                if not self.count_abs:
                    num_evt = len(events) * self.count // 100
                delays = self.randomizer(num_evt)[:num_evt]
                numpy.random.shuffle(events)
                with open(dataset.realPath("delay-management/Delays-Events.giv"), "wt") as f:
                    writer = LinTimCSVWriter(f)
                    writer.write("# RobTim random delays")
                    for (evt, delay) in zip(events, delays):
                        if int(delay) != 0:
                            writer.write([evt[0], int(delay)])
                            totalDelay += int(delay)
                            totalDelayWeighted += int(delay) * evt[1]
                            totalPassengers += evt[1]
            
            yield {
                    **self.info,
                    "delays_generator" : "robtim_distribution",
                    "randomizer": self.randomizer,
                    "delays_count": self.count,
                    "delays_absolute_numbers": self.count_abs,
                    "delays_events": self.events,
                    "delays_activities": self.activities,
                    "delays_total": totalDelay,
                    "delays_total_weighted": totalDelayWeighted,
                    "delays_total_passengers": totalPassengers,
                    "delays_average": totalDelay / totalPassengers if totalPassengers > 0 else 0, 
                    "delays_average_weighted": totalDelayWeighted / totalPassengers if totalPassengers > 0 else 0
                   }
    
class PoissonDistScenarioGenerator(DistributionScenarioGenerator):
    """
    Generates poisson distributed delay scenarios    
    
    Formula: stretch * numpy.random.poisson(lam)
    
    :param lam: poisson distribution's lambda parameter
    :param stretch: stretch parameter
    
    :param count: number/percantage of events/activites which shall be delayed
    :param count_abs: whether count is absolute (True) or relative (False)
    :param events: whether events shall be delayed
    :param activities: whether activities shall be delays
    """
    def __init__(self, lam : float, stretch : float = 1, 
                 count : int = 10, count_abs : bool = True, events : bool = False, activities : bool = True):
        super().__init__(
                lambda n : stretch*numpy.random.poisson(lam, n),
                count = count,
                count_abs = count_abs,
                events = events,
                activities = activities,
                info = {"distribution" : "poisson", "lambda" : lam, "stretch" : stretch}
                )

class NormalDistScenarioGenerator(DistributionScenarioGenerator):
    """
    Generates normal distributed delay scenarios    
    
    Formula: numpy.random.normal(mean, deviation)
    
    :param mean: mean
    :param deviation: standard deviation
    :param count: number/percantage of events/activites which shall be delayed
    :param count_abs: whether count is absolute (True) or relative (False)
    :param events: whether events shall be delayed
    :param activities: whether activities shall be delays
    """
    def __init__(self, mean : float, deviation : float, 
                 count : int = 10, count_abs : bool = True, events : bool = False, activities : bool = True):
        super().__init__(
                functools.partial(numpy.random.normal, mean, deviation),
                count = count,
                count_abs = count_abs,
                events = events,
                activities = activities,
                info = {"distribution" : "normal", "mean" : mean, "deviation" : deviation}
                )

class TreeOnTrackScenarioGenerator(DistributionScenarioGenerator):
    """
    Generates delay scenarios with tree-on-track distribution.
    The amount of trees, e.g. the amount of acitivities/events is binomial 
    distributed with parameters lambda and stretch.
    The delay each tree causes is exponentially distributed with parameter beta.
    
    :param binomial_p: binomial p parameter in [0,1]
    :param exp_beta: exponential beta parameter
    :param events: whether events shall be delayed
    :param activities: whether activities shall be delays
    """
    def __init__(self, binomial_p : float, exp_beta : float, events : bool = False, activities : bool = True):
        def randomizer(n) :
            p = numpy.random.binomial(n, binomial_p)
            delays = numpy.random.exponential(exp_beta, p)
            return delays
        super().__init__(
                randomizer,
                count = 100,
                count_abs = False,
                events = events,
                activities = activities,
                info = {"distribution":"tree-on-track", "p" : binomial_p, "beta" : exp_beta}
                )