class UnrecoverableException(Exception):
    pass

class RecoverableException(Exception):
    pass

class EmbeddingsDatabaseConnectionException(UnrecoverableException):
    def __init__(self, message):
        self.message = message