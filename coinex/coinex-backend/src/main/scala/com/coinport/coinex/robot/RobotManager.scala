/**
 * Copyright 2014 Coinport Inc. All Rights Reserved.
 * Author: c@coinport.com (Chao Ma)
 */

package com.coinport.coinex.robot

import com.coinport.coinex.data._
import com.coinport.coinex.common.Constants._
import com.coinport.coinex.common.Manager
import Implicits._

class RobotManager extends Manager[RobotState] {

  var state = RobotState()

  override def getSnapshot = state

  override def loadSnapshot(s: RobotState) {
    state = s
  }

  def apply() = state

  def addRobot(robot: Robot) {
    state = state.addRobot(robot)
  }

  def removeRobot(id: Long): Option[Robot] = {
    val robot = state.getRobot(id)
    robot foreach {
      _ => state = state.removeRobot(id)
    }
    robot
  }

  def addRobotBrain(states: scala.collection.immutable.Map[String, String]): String = {
    val (brainId, resultState) = state.addRobotBrain(states)
    state = resultState
    brainId
  }

  def getAction(brainId: String, currState: String): Action = {
    state.robotBrainMap(brainId).brain(currState)
  }

  def updateMetrics(m: Metrics) {
    state = state.updateMetrics(m)
  }
}
