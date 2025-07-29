# Software Architecture and Extensible Design (COMP3003/6007)  
## Semester 2, 2024

---

## Overview

This repository contains the source code and documentation for **Assignment 1** and **Assignment 2** of the COMP3003/6007 unit.

---

## Assignment 1: Air Traffic Simulation with Multithreading


### Summary

The goal of Assignment 1 is to design and implement a multithreaded air traffic simulation system in Java. The simulation features:

- A 2D map with multiple airports and planes.
- Planes move between airports based on flight requests received asynchronously from external processes.
- Real-time plane movement updates displayed via a GUI (JavaFX or Swing).
- Plane servicing simulation after landing.
- Use of key multithreading concepts including blocking queues, thread pools, thread safety, and graceful thread termination.
- A comprehensive report describing design decisions, thread management, and proposed architectural solutions for an extended system with real-time global flight data visualization.

### Key Features

- GUI-based visualization of airports and planes.
- Concurrent handling of flight requests and plane servicing.
- Up-to-date statistics and smooth UI without freezing.
- Use of blocking queues and thread pools for efficient thread communication and task management.

---

## Assignment 2: 2D Maze/Puzzle Game Engine with Plugins, Scripts, and Internationalisation


### Summary

Assignment 2 involves creating a flexible 2D maze/puzzle game engine in Java, featuring:

- Game grid configuration loaded from a custom domain-specific language (DSL) input file.
- Support for items, obstacles, player movement, visibility, and goal achievement.
- Internationalisation with full support for any locale via IETF language tags.
- Localised UI text and in-game date display that advances per player move.
- Dynamic loading and execution of plugins (Java) and scripts (Python/Jython).
- Several required plugins/scripts implementing gameplay extensions such as teleportation, penalties, map revealing, and special prizes.
- Built using Gradle with separate subprojects for core game, API, and plugins.
- Input file parsing via JavaCC integrated into the build.

### Key Features

- Grid-based gameplay with inventory and obstacle logic.
- Player visibility mechanics with optional cheat mode.
- Robust internationalisation and date handling.
- Plugin and scripting system with event callbacks and API access.
- Gradle build system with modular project structure.

---

## Usage

Both assignments are implemented in Java, built and run using Gradle. Refer to each assignment's README or documentation folder for detailed build and run instructions.

---

*This repository is for educational purposes and is part of the assessment tasks for the COMP3003/6007 course at Curtin University.*
