package info.lindblad.radio.solver;

import info.lindblad.radio.model.Island;
import info.lindblad.radio.model.Point;
import info.lindblad.radio.model.ReceiverTower;
import info.lindblad.radio.model.TransmitterTower;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestMatrixSolver extends TestCase {

    public TestMatrixSolver(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestMatrixSolver.class);
    }

    /**
     * Test solving the known case from the problem statement where receiver two is slightly out of range.
     * <p>
     * The solution is to increase the power level of transmitter tower 4 to 5.
     * <p>
     * *   *   *   *   x   x   x   x   x   x
     * *   *   *   *   *   *   *   x   R2  x
     * *   *   *   *   *   *   *   x   x   x
     * T2  *   *   *   *   *   *   x   x   x
     * *   *   T1  T4  *   *   R3  x   x   x
     * *   *   *   *   *   *   *   x   x   x
     * *   *   *   *   *   *   *   x   x   x
     * *   T3  *   *   *   *   *   x   x   x
     * R1  *   *   *   x   x   x   x   x   x
     * *   *   *   *   x   x   x   x   x   x
     */
    public void testSolvingKnownBadCoverage() {
        Island island = new Island(10, 10);

        // Add transmitter towers
        island.addTransmitterTower(new TransmitterTower(1, new Point(2, 5), 1));
        island.addTransmitterTower(new TransmitterTower(2, new Point(0, 6), 3));
        island.addTransmitterTower(new TransmitterTower(3, new Point(1, 2), 2));
        TransmitterTower transmitterTowerFour = new TransmitterTower(4, new Point(3, 5), 3);
        island.addTransmitterTower(transmitterTowerFour);

        // Add receiver towers
        island.addReceiverTower(new ReceiverTower(1, new Point(0, 1)));
        ReceiverTower receiverTowerTwo = new ReceiverTower(2, new Point(8, 8));
        island.addReceiverTower(new ReceiverTower(3, new Point(6, 5)));
        island.addReceiverTower(receiverTowerTwo);

        Set<ReceiverTower> expectedReceiverTowersWithoutCoverage = new HashSet<>();
        expectedReceiverTowersWithoutCoverage.add(receiverTowerTwo);

        assertEquals(expectedReceiverTowersWithoutCoverage, Solver.getReceiverTowersWithoutCoverage(island));
        assertEquals(1, Solver.nbrOfReceiverTowersWithoutCoverage(island));
        assertEquals(3, island.getNbrOfReceiverTowers());

        Map<TransmitterTower, Integer> expectedRequiredTransmitterTowerChanges = new HashMap<>();
        expectedRequiredTransmitterTowerChanges.put(transmitterTowerFour, 5);

        MatrixSolver matrixSolver = new MatrixSolver();

        Map<TransmitterTower, Integer> requiredTransmitterTowerChanges = matrixSolver.getRequiredTransmitterTowerChanges(island);
        assertEquals(expectedRequiredTransmitterTowerChanges, requiredTransmitterTowerChanges);

        // Apply the suggested changes
        for (Map.Entry<TransmitterTower, Integer> change : requiredTransmitterTowerChanges.entrySet()) {
            island.getTransmitterTowers().get(change.getKey().getPoint()).setPower(change.getValue());
        }
        assertEquals(0, Solver.nbrOfReceiverTowersWithoutCoverage(island));
    }

    /**
     * Test solving for a case where receiver two and three are both out of range.
     *
     * An inefficient solution would be to increase the power level of transmitter tower five by one
     * and that of transmitter tower four by two.
     *
     * That would lead to an overall power level increase of three.
     *
     * A more efficient solution is to instead increase the power level of transmitter four by two and by
     * doing so covering both receiver two and three.
     *
     *   *   *   *   *   x   *   *   *   x   x
     *   *   *   *   *   x   *   T4  *   x   R2
     *   *   *   *   *   x   *   *   *   x   x
     *   T2  *   *   *   x   x   x   x   x   x
     *   *   *   T1  *   x   x   R3  x   x   x
     *   *   *   *   *   x   *   *   *   x   x
     *   *   *   *   *   x   *   T5  *   x   x
     *   *   T3  *   *   x   *   *   *   x   x
     *   R1  *   *   *   x   x   x   x   x   x
     *   *   *   *   *   x   x   x   x   x   x
     *
     */
//    public void testMultiReceiverCase() {
//        Island island = new Island(10, 10);
//
//        // Add transmitter towers
//        island.addTransmitterTower(new TransmitterTower(1, new Point(2, 5), 1));
//        island.addTransmitterTower(new TransmitterTower(2, new Point(0, 6), 3));
//        island.addTransmitterTower(new TransmitterTower(3, new Point(1, 2), 2));
//
//        TransmitterTower transmitterTowerFour = new TransmitterTower(4, new Point(6, 8), 1);
//        island.addTransmitterTower(transmitterTowerFour);
//
//        island.addTransmitterTower(new TransmitterTower(5, new Point(6, 3), 1));
//
//        // Add receiver towers
//        island.addReceiverTower(new ReceiverTower(1, new Point(0, 1)));
//        ReceiverTower receiverTowerTwo = new ReceiverTower(2, new Point(9, 8));
//        ReceiverTower receiverTowerThree = new ReceiverTower(3, new Point(6, 5));
//        island.addReceiverTower(receiverTowerTwo);
//        island.addReceiverTower(receiverTowerThree);
//
//        // Create set of receiver towers that are expected to be out of signal coverage
//        Set<ReceiverTower> expectedReceiverTowersWithoutCoverage = new HashSet<>();
//        expectedReceiverTowersWithoutCoverage.add(receiverTowerTwo);
//        expectedReceiverTowersWithoutCoverage.add(receiverTowerThree);
//
//        assertEquals(expectedReceiverTowersWithoutCoverage, Solver.getReceiverTowersWithoutCoverage(island));
//        assertEquals(2, Solver.nbrOfReceiverTowersWithoutCoverage(island));
//        assertEquals(3, island.getNbrOfReceiverTowers());
//
//        Map<TransmitterTower, Integer> expected = new HashMap<>();
//        expected.put(transmitterTowerFour, 3);
//
//        MatrixSolver matrixSolver = new MatrixSolver();
//
//        Map<TransmitterTower, Integer> requiredTransmitterTowerChanges = matrixSolver.getRequiredTransmitterTowerChanges(island);
//        assertEquals(expected, requiredTransmitterTowerChanges);
//
//        // Apply the suggested changes
//        for ( Map.Entry<TransmitterTower, Integer> change : requiredTransmitterTowerChanges.entrySet() ) {
//            island.getTransmitterTowers().get(change.getKey().getPoint()).setPower(change.getValue());
//        }
//        assertEquals(0, Solver.nbrOfReceiverTowersWithoutCoverage(island));
//    }

    /**
     * Test solving for a case where receiver one and two are both out of range.
     *
     * Transmitter tower one could increase its power level by three and cover both
     * of the receiver towers, but a more efficient solution is to increase the power levels
     * of transmitter tower two and three by one each to achieve an overall power level increase of two.
     *
     * *   T1  *   x   x   x   x
     * *   *   *   x   x   x   x
     * x   x   x   x   x   x   x
     * x   x   x   x   x   x   x
     * x   R1  x   x   x   R2  x
     * *   *   *   x   *   *   *
     * *   T2  *   x   *   T3  *
     */
    public void testFavourableMultiChangeCase() {
        Island island = new Island(7, 7);

        // Add transmitter towers
        island.addTransmitterTower(new TransmitterTower(1, new Point(1, 6), 1));
        TransmitterTower transmitterTowerTwo = new TransmitterTower(2, new Point(1, 0), 1);
        TransmitterTower transmitterTowerThree = new TransmitterTower(3, new Point(5, 0), 1);
        island.addTransmitterTower(transmitterTowerTwo);
        island.addTransmitterTower(transmitterTowerThree);

        // Add receiver towers
        ReceiverTower receiverTowerOne = new ReceiverTower(1, new Point(1, 2));
        ReceiverTower receiverTowerTwo = new ReceiverTower(2, new Point(5, 2));
        island.addReceiverTower(receiverTowerOne);
        island.addReceiverTower(receiverTowerTwo);

        // Create set of receiver towers that are expected to be out of signal coverage
        Set<ReceiverTower> expectedReceiverTowersWithoutCoverage = new HashSet<>();
        expectedReceiverTowersWithoutCoverage.add(receiverTowerOne);
        expectedReceiverTowersWithoutCoverage.add(receiverTowerTwo);

        assertEquals(expectedReceiverTowersWithoutCoverage, Solver.getReceiverTowersWithoutCoverage(island));
        assertEquals(2, Solver.nbrOfReceiverTowersWithoutCoverage(island));
        assertEquals(2, island.getNbrOfReceiverTowers());

        Map<TransmitterTower, Integer> expected = new HashMap<>();
        expected.put(transmitterTowerTwo, 2);
        expected.put(transmitterTowerThree, 2);

        MatrixSolver matrixSolver = new MatrixSolver();

        Map<TransmitterTower, Integer> requiredTransmitterTowerChanges = matrixSolver.getRequiredTransmitterTowerChanges(island);
        assertEquals(expected, requiredTransmitterTowerChanges);

        // Apply the suggested changes
        for (Map.Entry<TransmitterTower, Integer> change : requiredTransmitterTowerChanges.entrySet()) {
            island.getTransmitterTowers().get(change.getKey().getPoint()).setPower(change.getValue());
        }
        assertEquals(0, Solver.nbrOfReceiverTowersWithoutCoverage(island));
    }
}