#include <stdio.h>
#include <intrin.h>
#include <windows.h>
#include <tchar.h>


#define COUNT 10000000

ULONG WINAPI Switch(__in PVOID p){
	int c = COUNT;
	do{SwitchToThread();}while(--c);
	return 0;
}


ULONG WINAPI Delegate(__in PVOID p){
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
	SetThreadAffinityMask(GetCurrentProcess(), (1));
	SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_HIGHEST);
	
	
	TestThreadHandle = CreateThread( NULL,
									 0,
									 Delegate,
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

DWORD _tmain(DWORD argc, _TCHAR* argv[])
{
	GetInfoComputer();
	KnowTimeToSwitchWindowsThread();
	printf("------------------------------------------------");
	_gettchar();
	return 1;
}

