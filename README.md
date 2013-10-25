scipela
=======

A pipeline simulator written in scala
Follows a basic FSM which state gets captured in rising/falling edges:
- Rising: Decrease all timers, transition delay states
- Falling: Collect all items from internal workers -> Feed items to next job

Design
======
- Queue -> Entity containing a buffer of elements.
- Delay -> Delay representing the wait time for a Job to run
- Worker -> Delay that represents work being done on a specific item
- Job -> Entity relating a Queue to a list of Workers, given a time Delay

Running
=======
Default scala project using sbt
