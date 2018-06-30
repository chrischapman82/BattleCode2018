# BattleCode2018
Submission under AllDayFanclub for battlecode, an annual MIT competition.

A bot I created for the BattleCode2018 competition. Hosted annually by MIT to fight for a chance at $50,000 in prizes.

What is Battlecode?
Battlecode is a real-time strategy game, for which you will write an AI player. In Battlecode, two teams of virtual robots roam the screen managing resources and executing different offensive strategies against each other. Your AI player will need to strategically manage your robot army and control how your robots work together to defeat the enemy team. As a contestants, you will learn to use artificial intelligence, pathfinding, distributed algorithms, and network communications to make your player as competitive as possible.

Related information about BattleCode 2018:
[BattleCode Website](https://www.battlecode.org/#/)
[Project specs](https://s3.amazonaws.com/battlecode-2018/specs/battlecode-specs-2018.html)

Reflections:
In this process, due to the time limits, I learnt how to use A-star effectively. I also used Breadth First Search to create several maps containing directions from each square to key destinations.

Unfortunately, this year was not as well organised as the previous year. This lead to a frustrating time, where over the weeks competitors had to deal with:
1.  Having no replay system for the first 2 of 3 weeks. This made it hard to get good feedback on how the bot was performing. Thankfully, another student created an open source a replay viewer.
2.  Documentation was never written, making understanding the code very arduous.
3.  Docker problems plagued me throughout the tournament. I lost 2 days cumulative in patching, updating, clearing, removing and re-installing docker to run the BattleCode scaffold.
4.  Massive unit balance changes were implemented 2 days before the final international tournmanent, doubling unit build times, and making my rushing strategies much weaker. 
