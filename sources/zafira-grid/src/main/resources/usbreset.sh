#!/bin/bash
export udid=$1
echo udid: $udid


export DEVICE_BUS_TEMP=device_bus.tmp

#export informarmation about Bus/Device and serial into separate file
lsusb -v | grep -E '\<(Bus|iSerial)' > ${DEVICE_BUS_TEMP} 2>/dev/null


while read line
do
	# iSerial                 3 LGH63549585411
	export serial=`echo $line | grep iSerial |cut -d ' ' -f 3`

        if [ "${serial}" == "${udid}" ] ; then
		echo device found for reset: iSerial: $serial Bus: $bus Device: $device
		usbreset /dev/bus/usb/$bus/$device
		break;
        fi

        export bus=`echo $line | cut -d ' ' -f 2`
        export device=`echo $line | cut -d ' ' -f 4 | cut -d ':' -f 1 `

done < ${DEVICE_BUS_TEMP}


rm -f ${DEVICE_BUS_TEMP}
