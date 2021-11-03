package com.codecool.websocket.service.moveCheckers;

public class WallBetweenChecker {
    private CheckUtility checkUtility;

    public WallBetweenChecker(CheckUtility checkUtility) {
        this.checkUtility = checkUtility;
    }

    public boolean isWallBetween (int currentCellID, int targetCellID) {
        int smaller = Math.min(currentCellID, targetCellID);
        if (Math.abs(currentCellID - targetCellID) > 4) {
            for (int i = 0; i + smaller < Math.max(currentCellID, targetCellID); i += 17) {
                if (checkUtility.isWall(smaller + i)) {
                    return true;
                }
            }
        } else {
            for (int i = 1; i + smaller < Math.max(currentCellID, targetCellID); i += 2) {
                if (checkUtility.isWall(smaller + i)) {
                    return true;
                }
            }
        }
        return false;
    }

}
