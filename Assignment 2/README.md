# 2D Maze/Puzzle Game Engine  
*Software Architecture and Extensible Design (COMP3003/6007) — Semester 2, 2024*  

---

## Overview

This project implements a simple yet extensible 2D maze/puzzle game engine as part of Assignment 2 for the COMP3003/6007 unit. The game engine is designed with modular architecture principles, leveraging Gradle for build automation, JavaCC for domain-specific language parsing, and supports internationalisation, dynamic plugins, and scripting.

---

## What This Project Is

A console- or GUI-based 2D grid maze/puzzle game engine where a player navigates through a configurable grid containing items, obstacles, and a goal location. The game supports runtime loading of plugins and scripts that extend gameplay behavior dynamically without modifying core game code.

---

## What It Does

- Loads game configuration from a specially-designed input file using a custom DSL parser (JavaCC).  
- Renders the game grid with visible and hidden squares; reveals squares as the player moves.  
- Manages player movement (up, down, left, right) with inventory handling for acquired items.  
- Implements obstacle traversal restrictions requiring specific items to pass.  
- Supports internationalisation with locale switching and UI text translations.  
- Tracks in-game date/time advancing per player move.  
- Dynamically loads and runs plugins (Java classes) and scripts (Python/Jython) as specified in the input file.  
- Provides an API for plugins and scripts to interact with game state and respond to events such as player moves or item acquisition.

---

## How It Works

1. **Game Initialization**  
   The game is launched via Gradle with a command-line argument specifying the path to the input file (e.g., `./gradlew run --args="../input.utf8.map"`). The input file defines grid size, player start, goal location, items, obstacles, plugins, and scripts.

2. **Input Parsing**  
   A JavaCC-generated parser reads the input file written in a domain-specific language (DSL). It extracts all declarations and configures the game state accordingly, supporting UTF-8/16/32 encodings.

3. **Gameplay Loop**  
   - The player starts at the defined location with adjacent grid squares visible.  
   - The player inputs moves ("up", "down", "left", "right").  
   - Movement updates grid visibility, manages inventory on item acquisition, and enforces obstacle traversal rules.  
   - The in-game date increments with each move and is displayed in a localized format.

4. **Plugins and Scripts**  
   - Plugins are dynamically loaded via reflection based on fully-qualified class names.  
   - Scripts are executed using a scripting engine (Jython for Python).  
   - Both plugins and scripts can register callbacks for player moves, item acquisition, and UI interactions, and can query/modify game state via a defined API.

5. **Internationalisation**  
   The user can change the UI locale at any point during the game by entering an IETF language tag. The game supports translations for English and another language, with all UI text properly localized.

6. **Build & Run**  
   The project is organized into Gradle subprojects: core game, API, and plugins. This modular design facilitates extensibility and clear dependency management.

---

## Tech Stack

- **Language:** Java (core game, API, plugins)  
- **Build Tool:** Gradle (multi-module setup, including JavaCC integration)  
- **Parser Generator:** JavaCC (for DSL input file parsing)  
- **Scripting:** Jython (Python scripting support)  
- **Internationalisation:** Java Locale API with IETF language tag support  
- **Dynamic Loading:** Java Reflection for plugin loading  
- **Testing:** Console or GUI interface for gameplay (depending on implementation choice)  
- **Linting:** PMD static analysis integrated via Gradle  

---

## Project Structure

- **core:** Main game engine, game loop, and core logic  
- **api:** Interfaces for plugins and scripts to interact with the game  
- **plugins:** Multiple plugin implementations (`Teleport`, `Penalty`, `Reveal`, `Prize`)  
- **scripts:** Embedded Python scripts within demo input files  
- **parser:** JavaCC grammar file and generated parser classes  
- **resources:** Input map files (`demoinput.utf8.map`) with sample game configurations  
- **build.gradle:** Gradle build scripts managing dependencies and build lifecycle  

---

## What's Happening

- The player interacts with a grid maze configured via external files using a custom DSL.  
- Items can be collected, obstacles block movement unless specific items are held.  
- Plugins and scripts add special gameplay features like teleportation, penalties for slow moves, revealing hidden areas, and prize rewards.  
- The UI and messages adapt dynamically to the selected locale, including localized date display.  
- The project showcases clean architecture, extensibility through plugins/scripts, and build automation with Gradle.

---

## How to Run

1. Clone the repository.  
2. Place your input map files (e.g., `input.utf8.map`) in the project root.  
3. Use Gradle to build and run the game with:  
   ```bash
   ./gradlew run --args="../input.utf8.map"
4. Alternatively, install and run the game using:
   ```bash
   ./gradlew :core:install
   ./core/build/install/core/bin/core ../input.utf8.map
5. During gameplay, input moves and locale changes as per prompts.

## Notes
- The input DSL supports UTF-8, UTF-16, and UTF-32 encoded files, identified by filename extensions .utf8.map, .utf16.map, or .utf32.map.
- The parser handles whitespace and formatting flexibility in input files.
- The API allows for future expansion of plugins and scripting languages with minimal core changes.

