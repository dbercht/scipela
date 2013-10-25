scipela
=======

A pipeline simulator written in scala

Design
======
- Queue -> Entity containing a buffer of elements.
- Delay -> Delay representing the wait time for a Job to run
- Worker -> Delay that represents work being done on a specific item
- Job -> Entity relating a Queue to a list of Workers, given a time Delay

FSM: State gets captured in rising/falling edges:
- Rising: Decrease all delays and transition appropriate ones
- Falling: Collect all items from workers ready to transition and feed to succeeding Job(s) in pipeline.

Running
=======
Default scala project using sbt
