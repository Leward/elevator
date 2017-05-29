package eu.leward.elevator

import groovy.transform.CompileStatic

@CompileStatic
class Elevator {

    int floor
    boolean moving
    Direction direction
    Collection<PickupCall> pickupCalls = []
    Map<Integer, Boolean> floorButtons = [:]

    Elevator(int nbFloors) {
        for (int i = 1; i <= nbFloors; i++) {
            floorButtons[i] = false
        }
    }

    void requestPickup(PickupCall pickupCall) {
        pickupCalls.add(pickupCall)
        if (!moving) {
            moveTo(pickupCall.floor)
        }
    }

    void requestTrip(int targetFloor) {
        floorButtons[targetFloor] = true
        if (!moving) {
            moveTo(targetFloor)
        }
    }

    private void moveTo(int targetFloor) {
        moving = true
        driveMotors(targetFloor, onMoved)
    }

    private void driveMotors(int targetFloor, Closure<Integer> callback) {
        // Simulating the elevetors moving.
        // Let's assume it always take 5 seconds for the elevator to reach its next destination
        Timeout.setTimeout({ callback(targetFloor) }, 5000)
    }

    private Closure<Integer> onMoved = { int movedToFloor ->
        moving = false
        floor = movedToFloor
        // The good etiquette wants that the people entering the elevator lets the
        // people to exit the elevator first :)
        dropPassengers(floor)
        pickupPassengers(floor)
        getNextStop().ifPresent { int nextFloor ->
            moveTo(nextFloor)
        }
        return floor
    }

    private pickupPassengers(int floor) {
        pickupCalls.removeAll { it.floor == floor }
    }

    private dropPassengers(int floor) {
        floorButtons[floor] = false
    }

    private Optional<Integer> getNextStop() {
        def nextStops = getNextStopsInElevatorDirection()
        if (nextStops.size() == 0) {
            nextStops = getNextStopsOppositeToElevatorDirection()
        }
        return Optional.ofNullable(getClosestFloor(nextStops))
    }

    private List<Integer> getNextStopsInElevatorDirection() {
        def pickupFloors = pickupCalls
                .findAll({ it.direction == direction })
                .collect({ it.floor })
        def dropOffFloors = getButtonsInElevatorDirection()
                .entrySet()
                .collect({ it.key })
        return (pickupFloors + dropOffFloors).asList()
    }

    private List<Integer> getNextStopsOppositeToElevatorDirection() {
        def pickupFloors = pickupCalls
                .findAll({ it.direction != direction })
                .collect({ it.floor })
        def dropOffFloors = getButtonsOppositeToElevatorDirection()
                .entrySet()
                .collect({ it.key })
        return (pickupFloors + dropOffFloors).asList()
    }

    private Map<Integer, Boolean> getButtonsInElevatorDirection() {
        floorButtons.findAll {
            if (direction == Direction.UP) {
                return it.key > floor
            } else {
                return it.key < floor
            }
        }
    }

    private Map<Integer, Boolean> getButtonsOppositeToElevatorDirection() {
        floorButtons.findAll {
            if (direction == Direction.UP) {
                return it.key < floor
            } else {
                return it.key > floor
            }
        }
    }

    private Integer getClosestFloor(List<Integer> floors) {
        return floors.collect({ Math.abs(floor - it) })
                .sort()
                .first()
    }

}