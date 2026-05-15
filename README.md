# CyberPet Companion

A high-fidelity virtual pet application built with Jetpack Compose and Material 3, featuring a neon-cyberpunk aesthetic.

## Features

- **Pet Simulation**: Real-time simulation of pet needs (Hunger, Energy, Happiness, Health, Hygiene).
- **Economic System**: Earn coins through mini-games and interactions to buy food and upgrades.
- **Dynamic World**: Time of day and weather cycles that affect pet behavior.
- **Casino & Mini-games**: High-stakes casino games with risk analytics and various mini-games.
- **Deep Progression**: Missions, achievements, and a prestige system for long-term engagement.
- **Advanced AI**: "Brain Engine" for pet decision making and social media simulation.

## Tech Stack

- **UI**: Jetpack Compose, Material 3
- **Architecture**: MVVM, Clean Architecture
- **Dependency Injection**: Hilt
- **Local Database**: Room
- **Background Tasks**: WorkManager
- **Persistence**: DataStore
- **Networking/Images**: Coil
- **Logging**: Timber
- **Serialization**: Kotlinx Serialization

## Project Structure

- `data`: Repositories and local data source (Room & DataStore).
- `domain`: Business logic, models, and use cases.
- `ui`: Compose screens, components, view models, and themes.
- `di`: Hilt modules.
