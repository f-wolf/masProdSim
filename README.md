# masProdSim - a multi agent production simulation #

## Introduction ##

This simulation tool makes it possible to simulate a production 
environment as a multi agent system (MAS). In this case, each 
agent represents and controls one machine. The agents can be 
considered the software equivalent of human operators.
They independently make decisions about their next
actions. 

MAS offer nice properties like flexibility, scalability and 
robustness. However, they cannot achieve the same performance 
as centralized solutions because the agents only have access 
to partial information.

More information and a different scheduling solution can be 
found for example in the following paper 
by [Gabel and Riedmiller (2008)](http://ml.informatik.uni-freiburg.de/former/_media/publications/gr07.pdf).

## How to use ##

This simulation tool was designed to quickly test different 
scheduling algorithms for multi agent production environments. 
New schedulers can be tested by creating a new scheduler class 
which implements the "Scheduler"-Interface and is selected in 
the properties.

The scheduling problems are read in from a selected text file and are 
formatted like the job shop benchmarks from the [OR-library](http://people.brunel.ac.uk/~mastjjb/jeb/orlib/files/jobshop1.txt).
This means the machines have to be numbered continuously from 0 to 
n-1 for n machines. Each line of the file is one product. 
For every task, the machine number and the production time
are given as integers. All values are separated by spaces. 

This tool can be build with the command *mvn clean compile assembly:single* 
or run directly from an IDE. So far it was only tested on 
Ubuntu 16.04.

## Already implemented schedulers ##

The project currently contains two example schedulers: FiFo 
and EvolutionStrategy.

### FiFo ##

The FiFo (First in, First out) scheduler does not make any smart
scheduling decisions. The agents simply keep track of all of their
open tasks in a list and select the first element of the list
whenever they can start to fulfill the next task.


## EvolutionStrategy ##

This scheduler uses an artificial neural network (ANN) to rank 
the open tasks of an agent. The agents always select the highest
ranked task for the next production activity. The ANN is trained
by using the [evolution strategy approach proposed by OpenAI](https://blog.openai.com/evolution-strategies/).


