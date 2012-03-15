#include "ata.h"

#define Pause_for_400ns()                  {inb(STATUS_REGISTER);inb(STATUS_REGISTER);inb(STATUS_REGISTER);inb(STATUS_REGISTER);}

U32 ATA_read(U32 LBA, U16* buffer, U16 numberOfSectors)
{
	U16 nos=0;
	//1. Wait for BSY == 0
	while(inb(STATUS_REGISTER) & BSY_BIT);
	//2. Write 0xE0 (LBA mode, Master dev.) ored with 27:24 bits of the LBA address to port 6
	outb((MASTER | ((LBA >> WORD_AND_HALF_SHIFT) & NIBBLE_MASK)), HEAD_PORT);
	//3. Pause for 400ns
	Pause_for_400ns();
	//4. Write 23:16 bits of the LBA address to port 5
	outb( (LBA >> WORD_SHIFT) & BYTE_MASK , CYLINDER_HIGH);
	//5. Write 15:8 bits of the LBA address to port 4
	outb( (LBA >> BYTE_SHIFT) & BYTE_MASK , CYLINDER_LOW);
	//6. Write 7:0 bits of the LBA address to port 3
	outb( LBA & BYTE_MASK , SECTOR_NUMBER);
	//7. Write the number of sectors to read to port 2 (note: 0 means 256)
	outb( numberOfSectors , SECTOR_COUNT);
	//8. Write 0 (PIO mode) to port 1
	outb(PIO_MODE,FEATURES_REGISTER);
	//9. Write 0x20 to port 7
	outb(0x20,COMMAND_REGISTER);
	//10. Pause for 400ns
	Pause_for_400ns() ;
	
	while(nos<numberOfSectors)
	{
		//11. Wait for BSY == 0
		while(inb(STATUS_REGISTER) & BSY_BIT);
		//12. Wait for ERR == 1, DF == 1, or DRQ == 1
		//13.	If ERR == 1 or DF ==1 the operation failed
		if (inb(STATUS_REGISTER) & (ERR_BIT | DF_BIT))
		{
			return 1; //the operation failed;
		}
		//13.	else if DRQ == 1 data is ready to be read
		else if(inb(STATUS_REGISTER) & DRQ_BIT)
		{
			//14. Use rep insw to read 1 sector (256 words) from port 0
			
			rep_insw(DATA_PORT, &buffer[nos*TRF_WORDS], TRF_WORDS);
			++nos;
		}
		//15. If there all sector are read, exit; else goto 11			
	}
}







