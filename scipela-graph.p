datafile = './scipela/output_trace.csv'
statsfile = './scipela/output_stats.csv'

set datafile separator ","
set key autotitle columnheader

set autoscale y
set autoscale x
set multiplot layout 4,1

t=1
x=0

samples(x) = $0 > 4 ? 5 : ($0+1)
avg5(x) = (shift5(x), (back1+back2+back3+back4+back5)/samples($0))
shift5(x) = (back5 = back4, back4 = back3, back3 = back2, back2 = back1, back1 = x)

#
# Initialize a running sum
#
init(x) = (back1 = back2 = back3 = back4 = back5 = sum = 0)

set yrange [-1:]

plot datafile using 1:4 with lines,\
datafile using 1:5 with lines,\
datafile using 1:6 with lines
#datafile using 1:5 with lines,\
#datafile using 1:6 with lines,\
#datafile using 1:7 with lines,\
#datafile using 1:8 with lines,\
#datafile using 1:9 with lines,\
#datafile using 1:10 with lines

set grid x y2
set autoscale y
set autoscale y2
set y2tics nomirror

plot sum = init(0),\
datafile using 1:(avg5($2)) with dots axes x1y1,\
datafile using 1:(x=x+$2*t) with dots axes x1y2 


set yrange [0:10]
plot datafile using 1:3 with lines axes x1y1


set style arrow 1 nohead
plot statsfile using (0.0):1:(1.0):(0.0) with vectors arrowstyle 1,\
statsfile using (0.0):2:(1.0):(0.0) with vectors 


unset multiplot
