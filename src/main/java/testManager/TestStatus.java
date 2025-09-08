package testManager;

public enum TestStatus {
	PENDING{
		@Override
		public TestStatus setStatusTo() {
			return PENDING;
		}
	}, // test case uploaded
	INVALID // failed compilation
	{
		public TestStatus setStatusTo() {
			return INVALID;
		}
	},
	PASSED // test case passed successfully
	{
		@Override
		public TestStatus setStatusTo() {
			return PASSED;
		}
	}
	,
	FAILED // test step failed execute next
	{
		@Override
		public TestStatus setStatusTo() {
			return FAILED;
		}
	}
	,
	STOP_EXECUTION // test step failed , terminate execution of next test step
	{
		@Override
		public TestStatus setStatusTo() {
			return FAILED;
		}
	};
	
	public boolean shouldStop() {
		return this == STOP_EXECUTION || this == INVALID;
	}
	public TestStatus setStatusTo() {
		return this;
	}
	public boolean isFailed() {
		return this.shouldStop() || this == FAILED;
	}
	public boolean isPassed() {
		return this == PASSED;
	}
}

