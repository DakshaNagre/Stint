class Calendar:
    def __init__(self):
        self.taskBoardId = None
        self.taskTitle = None
        self.taskDescription = None
        self.taskDueDate = None   # MM/DD/YYYY
        self.taskDueTime = None   # HH:MM:SS, 24 hours format
        self.taskElapsed = False
        self.taskId = None