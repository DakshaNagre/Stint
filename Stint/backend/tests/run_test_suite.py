# importing required libraries
import unittest

# Import test classes
from .services.user_service_test import UserServiceTest
from .services.board_service_test import BoardServiceTest
from .services.swim_lane_service_test import SwimLaneServiceTest
from .services.task_service_test import TaskServiceTest

# Method to run all the unit tests
def run_test_suite():
    # Initializing a suite
    suite = unittest.TestSuite()

    # Adding test classes to suite
    suite.addTest(unittest.makeSuite(UserServiceTest))
    suite.addTest(unittest.makeSuite(BoardServiceTest))
    suite.addTest(unittest.makeSuite(SwimLaneServiceTest))
    suite.addTest(unittest.makeSuite(TaskServiceTest))

    # Initialzing and running the test runner
    runner = unittest.TextTestRunner()
    runner.run(suite)

# Starting the test suite
run_test_suite()
