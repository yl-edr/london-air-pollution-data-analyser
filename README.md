# 🗺️ London & UK Air Pollution Viewer

A desktop app to explore **historical UK air pollution** (2018–2023), compare **cities**, estimate **Tube-journey exposure**, fetch **real-time** global data, and even **predict future values**—all in a tabbed JavaFX UI. 

---

## 🚀 Features

* 🏙️ **Tabbed UI**: London Map, Pollution Statistics, UK Cities, Tube Journey, Real Time Data.  
* 🗺️ **Clickable maps**: colour-graded overlays per pollutant/year + pixel→grid lookups. 
* 📈 **Charts**: switch between **Line**, **Bar**, **Pie** to see trends and composition.  
* 🔮 **Predictions**: linear regression on 2018–2023 to forecast 2024–2030.  
* 🚇 **Tube journey**: quickest route (Zone 1) + PM2.5 comparison vs street level. 
* 🌍 **Real-time data**: OpenWeatherMap geocoding + air pollution (8 metrics) with compare view.  
* 🧪 **Tests**: JUnit for City UI bits and Tube route logic. 

---

## 🛠️ How It Works

1. **Data load.** On start, CSVs from `UKAirPollutionData/` are processed for `NO2`, `pm10`, `pm2.5`, and `Tube/`. Datasets are keyed by **city + year + pollutant** for fast lookups.  
2. **London/UK tabs.** `City` renders a city map (`resources/<City>.png`) and updates overlay as you change pollutant/year; London also exposes a **Predict** dialog.  
3. **Statistics.** `Chart` manages Line/Bar/Pie and updates series based on the selected datasets. 
4. **Tube journey.** `Tube` shows a Tube map (`resources/TubeMap.png`) and computes a route + exposure summary.  
5. **Real-time tab.** `ApiConnection` hits OpenWeatherMap’s **Geocoding** and **Air Pollution** endpoints; UI throttles requests and renders 8 metrics with color blocks.  

---

## 📁 Project Structure

```
├── AppWindow.java            # Main JavaFX app; loads tabs & data
├── City.java                 # Map UI per city + Predict button (London)
├── Chart.java                # Line / Bar / Pie chart manager
├── DataAggregator.java       # Load/filter CSVs; keyed by city/year/pollutant
├── RealTimeDataTab.java      # Search + realtime metrics + compare
├── ApiConnection.java        # OpenWeatherMap API calls
├── Tube.java                 # Tube map UI and journey planner
├── TubeSystem*.java          # Route calculation + tests
├── MapImage.java, Data*.java # Map colouring, CSV load, filtering...
├── style.css                 # JavaFX styling (uses images from /resources)
├── UKAirPollutionData/       # CSVs             
└── resources/                # PNGs
```

---

## 📋 Requirements

* **JDK 17+**
* **JavaFX 17+** (controls/graphics; add to module path when running from CLI). 
* **org.json** library (for API JSON parsing). 

> IDEs like IntelliJ make JavaFX setup easiest. If you prefer CLI, see the example below.

---

## 🔑 Setup

1. **Clone**

```bash
git clone https://github.com/yl-edr/london-air-pollution-data-analyser.git
cd london-air-pollution-data-analyser
```

2. **Place data & resources**

* Put CSVs inside `UKAirPollutionData/NO2/`, `UKAirPollutionData/pm10/`, `UKAirPollutionData/pm2.5/`, and tube data in `UKAirPollutionData/Tube/`. 
* Ensure images live in `resources/` (e.g., `London.png`, `TubeMap.png`, `welcomeImage.png`). The CSS references `resources/welcomeImage.png`.   

3. **API key (real-time tab)**
   Edit `ApiConnection.java` and replace the `apiKey` constant with your **OpenWeatherMap** key. 

---

## ▶️ Usage

### Run (IDE recommended)

Open the project, mark `resources/` as a *Resources Root*, add **JavaFX** SDK, run `AppWindow`. 

### Run (CLI example)

```bash
# Set paths (adjust for your machine)
export JAVAFX_HOME=/path/to/javafx-sdk-17/lib
export JSON_JAR=/path/to/json.jar

# Compile
javac --module-path "$JAVAFX_HOME" --add-modules javafx.controls,javafx.graphics \
  -cp "$JSON_JAR" *.java

# Run
java --module-path "$JAVAFX_HOME" --add-modules javafx.controls,javafx.graphics \
  -cp ".:$JSON_JAR" AppWindow
```

### What you’ll see

* **London Map**: pick pollutant & year; hover to see grid code/value; click **Predict** for a future year.  
* **Statistics**: toggle **Line/Bar/Pie** charts for 2018–2023 trends. 
* **UK Cities**: switch among Manchester, Bristol, Leeds, Birmingham (combobox appears only when not on London). 
* **Tube Journey**: enter start/end stations to get route + PM2.5 comparison.  
* **Real-Time**: search, pick a city, view 8 metrics; add to **Compare** to chart multiple cities.  

---

## 🧪 Tests

* **CityTest**: UI/component checks and boundaries. 
* **TubeSystemTest**: direct, transfer, long, invalid, and same-station journeys. 

---

## 📜 License

This project is licensed under the MIT License — feel free to use and modify it. \
***Author: Anton Davidouski, Rom Steinberg, Nicolás Alcalá Olea, Yaal Edrey***
