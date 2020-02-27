package net.lintim.model;

import net.lintim.model.impl.TestEdge;
import net.lintim.model.impl.TestNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 */
public abstract class PathTest {
    protected Path<TestNode, TestEdge> directedPath;
    protected Path<TestNode, TestEdge> undirectedPath;

    @Before
    public abstract void supplyPaths();

    @Test
    public void canAddToFrontDirected() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestNode node4 = new TestNode(4);
        TestNode node5 = new TestNode(5);
        TestEdge edge1 = new TestEdge(1, node1, node2);
        TestEdge edge2 = new TestEdge(2, node2, node3);
        TestEdge edge3 = new TestEdge(3, node3, node4);
        TestEdge edge4 = new TestEdge(4, node4, node5);
        assertEquals(0, directedPath.getEdges().size());
        assertEquals(0, directedPath.getNodes().size());
        assertTrue(directedPath.addFirst(edge4));
        assertEquals(1, directedPath.getEdges().size());
        assertEquals(2, directedPath.getNodes().size());
        assertTrue(directedPath.addFirst(edge3));
        assertEquals(2, directedPath.getEdges().size());
        assertEquals(3, directedPath.getNodes().size());
        assertTrue(directedPath.addFirst(Arrays.asList(edge1, edge2)));
        assertEquals(4, directedPath.getEdges().size());
        assertEquals(5, directedPath.getNodes().size());
    }

    @Test
    public void canAddToEndDirected() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestNode node4 = new TestNode(4);
        TestNode node5 = new TestNode(5);
        TestEdge edge1 = new TestEdge(1, node1, node2);
        TestEdge edge2 = new TestEdge(2, node2, node3);
        TestEdge edge3 = new TestEdge(3, node3, node4);
        TestEdge edge4 = new TestEdge(4, node4, node5);
        assertEquals(0, directedPath.getEdges().size());
        assertEquals(0, directedPath.getNodes().size());
        assertTrue(directedPath.addLast(edge1));
        assertEquals(1, directedPath.getEdges().size());
        assertEquals(2, directedPath.getNodes().size());
        assertTrue(directedPath.addLast(edge2));
        assertEquals(2, directedPath.getEdges().size());
        assertEquals(3, directedPath.getNodes().size());
        assertTrue(directedPath.addLast(Arrays.asList(edge3, edge4)));
        assertEquals(4, directedPath.getEdges().size());
        assertEquals(5, directedPath.getNodes().size());
    }

    @Test
    public void canRemoveEdgeFrontDirected() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestNode node4 = new TestNode(4);
        TestNode node5 = new TestNode(5);
        TestEdge edge1 = new TestEdge(1, node1, node2);
        TestEdge edge2 = new TestEdge(2, node2, node3);
        TestEdge edge3 = new TestEdge(3, node3, node4);
        TestEdge edge4 = new TestEdge(4, node4, node5);
        directedPath.addFirst(Arrays.asList(edge1, edge2, edge3, edge4));
        assertEquals(4, directedPath.getEdges().size());
        assertEquals(5, directedPath.getNodes().size());
        assertTrue(directedPath.remove(edge1));
        assertEquals(3, directedPath.getEdges().size());
        assertEquals(4, directedPath.getNodes().size());
        assertTrue(directedPath.remove(Arrays.asList(edge2, edge3)));
        assertEquals(1, directedPath.getEdges().size());
        assertEquals(2, directedPath.getNodes().size());
        assertTrue(directedPath.remove(edge4));
        assertEquals(0, directedPath.getEdges().size());
        assertEquals(0, directedPath.getNodes().size());
    }

    @Test
    public void canRemoveEdgeEndDirected() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestNode node4 = new TestNode(4);
        TestNode node5 = new TestNode(5);
        TestEdge edge1 = new TestEdge(1, node1, node2);
        TestEdge edge2 = new TestEdge(2, node2, node3);
        TestEdge edge3 = new TestEdge(3, node3, node4);
        TestEdge edge4 = new TestEdge(4, node4, node5);
        directedPath.addFirst(Arrays.asList(edge1, edge2, edge3, edge4));
        assertEquals(4, directedPath.getEdges().size());
        assertEquals(5, directedPath.getNodes().size());
        assertTrue(directedPath.remove(edge4));
        assertEquals(3, directedPath.getEdges().size());
        assertEquals(4, directedPath.getNodes().size());
        assertTrue(directedPath.remove(Arrays.asList(edge3, edge2)));
        assertEquals(1, directedPath.getEdges().size());
        assertEquals(2, directedPath.getNodes().size());
    }

    @Test
    public void cannotAddUnfittingEdgeDirected() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestEdge edge1 = new TestEdge(1, node2, node1);
        TestEdge edge2 = new TestEdge(2, node2, node3);
        assertTrue(directedPath.addFirst(edge1));
        assertEquals(1, directedPath.getEdges().size());
        assertEquals(2, directedPath.getNodes().size());
        assertFalse(directedPath.addFirst(edge2));
        assertEquals(1, directedPath.getEdges().size());
        assertEquals(2, directedPath.getNodes().size());
        assertFalse(directedPath.addLast(edge2));
        assertEquals(1, directedPath.getEdges().size());
        assertEquals(2, directedPath.getNodes().size());
    }

    @Test
    public void canAddToFrontUndirected() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestNode node4 = new TestNode(4);
        TestNode node5 = new TestNode(5);
        TestEdge edge1 = new TestEdge(1, node2, node1, false);
        TestEdge edge2 = new TestEdge(2, node2, node3, false);
        TestEdge edge3 = new TestEdge(3, node4, node3, false);
        TestEdge edge4 = new TestEdge(4, node4, node5, false);
        assertEquals(0, undirectedPath.getEdges().size());
        assertEquals(0, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.addFirst(edge4));
        assertEquals(1, undirectedPath.getEdges().size());
        assertEquals(2, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.addFirst(edge3));
        assertEquals(2, undirectedPath.getEdges().size());
        assertEquals(3, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.addFirst(Arrays.asList(edge1, edge2)));
        assertEquals(4, undirectedPath.getEdges().size());
        assertEquals(5, undirectedPath.getNodes().size());
    }

    @Test
    public void canAddToEndUndirected() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestNode node4 = new TestNode(4);
        TestNode node5 = new TestNode(5);
        TestEdge edge1 = new TestEdge(1, node2, node1, false);
        TestEdge edge2 = new TestEdge(2, node2, node3, false);
        TestEdge edge3 = new TestEdge(3, node4, node3, false);
        TestEdge edge4 = new TestEdge(4, node4, node5, false);
        assertEquals(0, undirectedPath.getEdges().size());
        assertEquals(0, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.addLast(edge1));
        assertEquals(1, undirectedPath.getEdges().size());
        assertEquals(2, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.addLast(edge2));
        assertEquals(2, undirectedPath.getEdges().size());
        assertEquals(3, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.addLast(Arrays.asList(edge3, edge4)));
        assertEquals(4, undirectedPath.getEdges().size());
        assertEquals(5, undirectedPath.getNodes().size());
    }

    @Test
    public void canRemoveEdgeFrontUndirected() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestNode node4 = new TestNode(4);
        TestNode node5 = new TestNode(5);
        TestEdge edge1 = new TestEdge(1, node2, node1, false);
        TestEdge edge2 = new TestEdge(2, node2, node3, false);
        TestEdge edge3 = new TestEdge(3, node4, node3, false);
        TestEdge edge4 = new TestEdge(4, node4, node5, false);
        undirectedPath.addFirst(Arrays.asList(edge1, edge2, edge3, edge4));
        assertEquals(4, undirectedPath.getEdges().size());
        assertEquals(5, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.remove(edge1));
        assertEquals(3, undirectedPath.getEdges().size());
        assertEquals(4, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.remove(Arrays.asList(edge2, edge3)));
        assertEquals(1, undirectedPath.getEdges().size());
        assertEquals(2, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.remove(edge4));
        assertEquals(0, undirectedPath.getEdges().size());
        assertEquals(0, undirectedPath.getNodes().size());
    }

    @Test
    public void canRemoveEdgeEndUndirected() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestNode node4 = new TestNode(4);
        TestNode node5 = new TestNode(5);
        TestEdge edge1 = new TestEdge(1, node2, node1, false);
        TestEdge edge2 = new TestEdge(2, node2, node3, false);
        TestEdge edge3 = new TestEdge(3, node4, node3, false);
        TestEdge edge4 = new TestEdge(4, node4, node5, false);
        undirectedPath.addFirst(Arrays.asList(edge1, edge2, edge3, edge4));
        assertEquals(4, undirectedPath.getEdges().size());
        assertEquals(5, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.remove(edge4));
        assertEquals(3, undirectedPath.getEdges().size());
        assertEquals(4, undirectedPath.getNodes().size());
        assertTrue(undirectedPath.remove(Arrays.asList(edge3, edge2)));
        assertEquals(1, undirectedPath.getEdges().size());
        assertEquals(2, undirectedPath.getNodes().size());
    }

    @Test
    public void cannotAddUnfittingEdgeUndirected() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestNode node4 = new TestNode(4);
        TestEdge edge1 = new TestEdge(1, node3, node2, false);
        TestEdge edge2 = new TestEdge(2, node2, node1, false);
        TestEdge edge3 = new TestEdge(1, node3, node4, false);
        assertTrue(undirectedPath.addFirst(Arrays.asList(edge1, edge2)));
        assertEquals(2, undirectedPath.getEdges().size());
        assertEquals(3, undirectedPath.getNodes().size());
        assertFalse(undirectedPath.addLast(edge3));
        assertEquals(2, undirectedPath.getEdges().size());
        assertEquals(3, undirectedPath.getNodes().size());
    }

    @Test
    public void canResetUnfittingPath() {
        TestNode node1 = new TestNode(1);
        TestNode node2 = new TestNode(2);
        TestNode node3 = new TestNode(3);
        TestNode node4 = new TestNode(4);
        TestNode node5 = new TestNode(5);
        TestEdge edge1 = new TestEdge(1, node1, node2);
        TestEdge edge2 = new TestEdge(2, node2, node3);
        TestEdge edge3 = new TestEdge(3, node3, node4);
        TestEdge edge4 = new TestEdge(4, node4, node5);
        assertTrue(directedPath.addFirst(edge1));
        assertEquals(1, directedPath.getEdges().size());
        assertEquals(2, directedPath.getNodes().size());
        assertFalse(directedPath.addLast(Arrays.asList(edge2, edge4)));
        assertEquals(1, directedPath.getEdges().size());
        assertEquals(2, directedPath.getNodes().size());

    }
}
