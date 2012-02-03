#ifndef __ATA_H__
#define __ATA_H__

#include "io.h"
#include "Types.h"

U32 ATA_read(U16 * destination_buffer, U32 LBA, U16 nr_sectors);

#endif
