package view;

import common.Buffer;
import common.ComponentBase;
import common.EarthGridProperties;
import common.IBuffer;
import common.IGrid;
import common.EarthGridProperties.EarthGridProperty;
import messaging.Message;
import messaging.events.SimResultMessage;
import messaging.events.ViewPauseSimMessage;
import messaging.events.ViewResumeSimMessage;

public class View extends ComponentBase {

	// 1e-12f;
	private final float STABLE_THRESHOLD = 0f;
	
	// set true to instrument stats (NOTE: some of these will change execution timing)
	private final boolean STATISTIC_MODE = false; 
	
	EarthDisplay display = null;

	// set to true when initial conditions are overcome
	boolean steadyState = false; 
	
	// Profiling fields
	float statInterval = 1.0f;
		
	// Steady state assumed when when average equator temperature stabilizes
	float lastEquatorAverage = 0.0f;
	float presentationInterval;
	int timeStep;
	
	// used to throttle presentation rate
	long lastDisplayTime = 0;
	long lastStatTime = 0;
	long maxUsedMem = 0;
	long startWallTime;
	long startCpuTime;
	long presentationCnt = 1;

	public View(EarthGridProperties simProp) {
		
		this.timeStep = simProp.getPropertyInt(EarthGridProperty.SIMULATION_TIME_STEP);
		this.presentationInterval = simProp.getPropertyFloat(EarthGridProperty.PRESENTATION_RATE);
		this.display = new EarthDisplay();
		display.display(simProp.getPropertyInt(EarthGridProperty.GRID_SPACING), timeStep);
		display.update((IGrid) null);
		// Setup message subscriptions
		pub.subscribe(SimResultMessage.class, this);
	}

	@Override
	public void dispatchMessage(Message msg) {
		if (msg instanceof SimResultMessage) {
			process((SimResultMessage) msg);
		} else {
			System.err.printf("WARNING: No processor specified in class %s for message %s\n",
					this.getClass().getName(), msg.getClass().getName());
		}
	}

	static int addCnt = 0;
	private void process(SimResultMessage msg) {
		// Add result to buffer for later presentation
		try {
//			System.out.printf("Adding message to buffer: %d %d\n", addCnt++, Buffer.getBuffer().getRemainingCapacity());
			Buffer.getBuffer().add(msg.result);
			// Throttle down the simulator if we're getting close to capacity
			if(Buffer.getBuffer().getRemainingCapacity() < Buffer.getBuffer().getCapacity()*0.3) {
				pub.send(new ViewPauseSimMessage());
			}
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
	}


	@Override
	public void runAutomaticActions() throws InterruptedException {
		// Don't do anything if enough time hasn't passed for us to display
		// another datapoint

		long curTime = System.nanoTime();
		if ((curTime - lastDisplayTime) * 1e-9 < presentationInterval) {
			return;
		}

		// Check to see if there is anything in the data queue to process
		IGrid data = null;
		data = Buffer.getBuffer().get();
		if (data != null) {
			// Ask un-throttle the simulator if we're getting low on buffer entries
			if(Buffer.getBuffer().getRemainingCapacity() > 0.6*Buffer.getBuffer().getCapacity()) {
				pub.send(new ViewResumeSimMessage());
			}
			if (STATISTIC_MODE) {

				if (!steadyState && steadyStateReached(data)) {
					steadyState = true;
					System.out.printf("========STABLE REACHED!========: %d %d\n",
							data.getCurrentTime(), data.getCurrentTime()/timeStep);
				}

				// Sample memory usage periodically
				if ((curTime - lastStatTime) * 1e-9 > statInterval) {
					float wallTimePerPresentation = (float) (System.nanoTime() - startWallTime)
							/ presentationCnt;
					System.out.printf("walltime/present (msec): %f\n",
							wallTimePerPresentation / 1e6);
					Runtime runtime = Runtime.getRuntime();
					System.gc();
					maxUsedMem = Math.max(maxUsedMem, runtime.totalMemory()
							- runtime.freeMemory());
					System.out.printf("usedMem: %.1f\n", maxUsedMem / 1e6);
					lastStatTime = curTime;

					IBuffer b = Buffer.getBuffer();
					System.out.printf("Buffer fill status: %d/%d\n",
							b.size() + 1, b.size() + b.getRemainingCapacity());

					startWallTime = System.nanoTime();
					presentationCnt = 0;

				}
				presentationCnt++;
			}
			present(data);
			lastDisplayTime = curTime;
		}
	}

	private void present(IGrid data) {

		display.update(data);
	}

	public void close() {
		// destructor when done with class
		display.close();
	}

	public boolean steadyStateReached(IGrid grid) {
		float equatorAverage = 0.0f;
		int eqIdx = grid.getGridHeight() / 2;
		for (int i = 0; i < grid.getGridWidth(); i++) {
			equatorAverage += grid.getTemperature(i, eqIdx);
		}
		equatorAverage /= grid.getGridWidth();

		boolean stable = false;

		if (Math.abs(equatorAverage - lastEquatorAverage) <= STABLE_THRESHOLD) {
			stable = true;
		}
		lastEquatorAverage = equatorAverage;
		return stable;

	}
}
