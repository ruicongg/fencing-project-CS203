# fencing-project-203

Our vision is to develop a software solution that enhances the current fencing tournament management system by offering a more efficient and fairer experience for both players and administrators. This platform aims to streamline tournament settings, improve player matchmaking, and provide a visually immersive user interface, to facilitate a fairer and more enjoyable process for all participants.

Player Features
● Account registration, Login authentication (JWT token), Viewing profile, Tournament
registration, Dashboard to receive updates and real-time player statistics 

Player Features (additional feature)
● Indication of availabilities for tournaments, preference towards selected weapon type(s) and number of events for best-fit assignment of tournament(s) based on ELO after the sign up deadlines close, providing players with tournament variety, while optimising for fair yet engaging competition

Admin Features
● Create, Read, Update, Delete tournaments, tournament details and match results
using SpringBoot and Java Persistence API Database Management
● Use of Amazon RDS for MySQL to operate and scale a database on cloud. 
● Database used to store user, match and tournaments information 

Individual ranking system (ELO ranking system)
● Used to determine the seeding of players into the group stage
● ELO of each player is adjusted after every tournament
● The ELO system focuses on the quality of a fencer's wins and losses, in essence,
skill level, rather than their participation rate, unlike the current ranking system 

Matchmaking algorithm
● We will have 2 separate stages: group stage and knockout stage, using ELO and tournament results for matchmaking respectively
● Group stage: Players with similar ELO will be evenly distributed into different groups, allowing players of different ELO to compete in the same group in a round-robin style
● Knockout stage: Higher-seeded players will then be pitted against the lower ones until there is a final winner

Web UI (additional feature)
● We believe that this feature will be feasible as we have prior experience working front-end and will be utilising React.js and JavaScript

Microservices-based design and scalable architecture (additional features)
● This will be used to separate our different functions (eg. matchmaking from CRUD
functions), allowing for scalability for player management independently from other
services, especially during registration spikes, and service-specific bug fixes
● With Spring Boot’s built-in features to handle microservices architecture and load
balancing tools like AWS elastic load balancing, we believe that
implementing this feature will be feasible. 

Security (additional feature)
● JWT token: JWTs are widely used and supported across various programming languages and frameworks. Additionally, Spring Boot integrates seamlessly with Spring Security, ensuring a comprehensive security framework for our project
● HTTPS: The implementation of HTTPS will be feasible due to its similarity to HTTP, and is integrated seamlessly through Spring Boot