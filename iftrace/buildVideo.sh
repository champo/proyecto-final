#! /bin/bash

# Utility script to transform IFTrace output into a useful video

if [ $# -lt 5 ]; then
    echo "usage: $0 <input folder> <output folder> <image type> <number of components> <first frame>";
    exit 1;
fi

j=$5;
out=1;
frame_number=`printf "%05d" $out`;
components=`printf "%02d" $4`;
for i in $(ls $1); do
	number=`printf "%05d" $j`;
	
	if [ -f $1/$number/seg_label_$components.$3 ]; then
		cp $1/$number/seg_label_$components.$3 $2/frame_$frame_number.$3
		let out=$out+1;
		frame_number=`printf "%05d" $out`;
	else 
		for r in $(ls $1/$number/recovery_seg_color_try*); do
			cp $r $2/frame_$frame_number.$3
			let out=$out+1;
			frame_number=`printf "%05d" $out`;
		done
	fi
	echo $number;
	let j=$j+1;
done

ffmpeg -f image2 -i $2/frame_%05d.ppm $2/output.mp4
