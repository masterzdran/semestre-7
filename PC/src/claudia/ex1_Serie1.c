#include <stdio.h>
#include <intrin.h>
#include <windows.h>

#include "../Header/UThread.h"
#include "../Header/List.h"

#define COUNT 10000000

typedef struct _TimeOfSwitch{
	DWORD64 initTime;
	DWORD64 endTime;
}TimeOfSwitch, *PTimeOfSwitch;//, ATimeOfSwitch[1];

VOID JustYield(__in UT_ARGUMENT Argument){
	int c = 0;
	do{
		UtYield();
	}while(c++!=COUNT);
}

ULONG WINAPI SwitchingUThreads(__in PVOID p){
	/*create 2 uthreads and each one call the method Yield to switch uthread
	use - GetTickCount Function - http://msdn.microsoft.com/en-us/library/ms724408(v=vs.85).aspx */
	int finalTime;
	ULONG timeEnd, timeStart;
	__int64 startCycleClock, endCycleClock;
	printf("-----------------------------------------\n");
	printf("Switching UThreads\n");
	UtCreateThread(JustYield, NULL);
	UtCreateThread(JustYield, NULL);
	timeStart = GetTickCount();
	startCycleClock = __rdtsc();
	UtRun();
	timeEnd = GetTickCount();
	endCycleClock = __rdtsc();
	finalTime = (int)(((timeEnd-timeStart) * 1000000.0) / (COUNT*2));
	printf("Absolute Time is %d ns\n", finalTime);
	printf("Number of Cycles %d\n",(endCycleClock-startCycleClock)/(COUNT*2));
	return 0;
}

void KnowTimeToSwitchUThread(){
	HANDLE TestThreadHandle;
	SetThreadAffinityMask(GetCurrentProcess(), (1 << 0));
	SetPriorityClass(GetCurrentProcess(), HIGH_PRIORITY_CLASS);
	//SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_HIGHEST);
	
	TestThreadHandle = CreateThread( NULL,
									 0,
									 SwitchingUThreads,
									 NULL,
									 CREATE_SUSPENDED,
									 NULL );
	ResumeThread(TestThreadHandle);
	WaitForSingleObject(TestThreadHandle, INFINITE);
	CloseHandle(TestThreadHandle);
}

ULONG WINAPI Switch(__in PVOID p){
	int c = COUNT;
	do{
		SwitchToThread();
	}while(--c);
	return 0;
}

ULONG WINAPI SwitchingWindowsThreads(__in PVOID p){
	/*SwitchToThread Function - http://msdn.microsoft.com/en-us/library/ms686352(v=vs.85).aspx */
	int finalTime;
	ULONG timeEnd, timeStart;
	__int64 startCycleClock, endCycleClock;
	HANDLE t1, t2;
	printf("-----------------------------------------\n");
	printf("Switching Windows Threads\n");
	t1 = CreateThread( NULL, 0, Switch, NULL, CREATE_SUSPENDED, NULL);
	t2 = CreateThread( NULL, 0, Switch, NULL, CREATE_SUSPENDED, NULL);

	startCycleClock = __rdtsc();
	timeStart = GetTickCount();
	
	ResumeThread(t1);
	ResumeThread(t2);
	WaitForSingleObject(t1, INFINITE);
	WaitForSingleObject(t2, INFINITE);

	timeEnd = GetTickCount();
	endCycleClock = __rdtsc();

	finalTime = (int)(((timeEnd-timeStart) * 1000000) / (COUNT*2));
	printf("Absolute Time is %d ns\n", finalTime);
	printf("Number of Cycles %d\n",(endCycleClock-startCycleClock)/(COUNT*2));
	return 0;
}

void KnowTimeToSwitchWindowsThread(){
	HANDLE TestThreadHandle;
	SetThreadAffinityMask(GetCurrentProcess(), (1 << 0));
	SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_HIGHEST);
	
	
	TestThreadHandle = CreateThread( NULL,
									 0,
									 SwitchingWindowsThreads,
									 NULL,
									 CREATE_SUSPENDED,
									 NULL );
	ResumeThread(TestThreadHandle);
	WaitForSingleObject(TestThreadHandle, INFINITE);
	CloseHandle(TestThreadHandle);
}

void GetInfoComputer(){
	SYSTEM_INFO si; char * ar = "Unknown architecture";
	GetSystemInfo(&si);
	printf("Number Of Processors: %d\n", si.dwNumberOfProcessors);
	if(si.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_INTEL)ar = "x86";
	else{
		if(si.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_IA64) ar = "Intel Itanium-based";
		else{
			if(si.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_AMD64) ar = "x64 (AMD or Intel)";
		}
	}
	printf("Processor Arquitecture: %s\n",ar);
}

VOID main ()
{
	GetInfoComputer();
	KnowTimeToSwitchUThread();
	KnowTimeToSwitchWindowsThread();
}

