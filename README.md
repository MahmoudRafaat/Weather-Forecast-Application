# Rasd 🌤️ | Smart Weather Forecast

Rasd is a robust Android application designed to provide accurate, real-time weather forecasts with an intuitive user experience. It empowers users to explore detailed weather information for any location globally and plan their activities accordingly.

Built with a focus on **Reactive Programming** and **Clean Architecture principles (MVVM)**, Rasd ensures a smooth user experience with offline access to cached weather data.

---

## 📥 Download App
You can download the latest version of the Rasd APK from the link below:

[**Download Rasd APK**](https://drive.google.com/drive/folders/1jWjnp1n4RQQ_VMnPAAqQNMXy5oJA4Q5Y?usp=sharing)

---

## 📸 App Screenshots

<table style="width: 100%; text-align: center;">
  <tr>
    <td><b>Splash Screen</b></td>
    <td><b>Home Dashboard</b></td>
    <td><b>Settings</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/fd4ea235-e45a-4f2e-90ab-87b072520ec9" width="200" alt="Splash" /></td>
    <td><img src="https://github.com/user-attachments/assets/29308a74-d276-49ad-9175-a6b76443cf38" width="200" alt="Home" /></td>
    <td><img src="https://github.com/user-attachments/assets/4687104a-1871-4c32-abab-5086a1faaee6" width="200" alt="Settings" /></td>
  </tr>
  <tr>
    <td><b>Search & Location</b></td>
    <td><b>Favorites</b></td>
    <td><b>Weather Alerts</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/f81c35c8-32ed-4d1d-82d5-b5b921c237be" width="200" alt="Map" /></td>
    <td><img src="https://github.com/user-attachments/assets/d751bdff-8305-4930-828a-d9b5ae4a5e43" width="200" alt="Favorites" /></td>
    <td><img src="https://github.com/user-attachments/assets/01762405-7f14-417c-bad7-5192f1f6f775" width="200" alt="Alerts" /></td>
  </tr>
</table>

---

## ✨ Key Features

- **🌍 Global Weather Data**: Access real-time weather information from anywhere in the world via **OpenWeatherMap API**.
- **📍 Smart Location Detection**: GPS-based automatic location detection or manual location selection through interactive maps.
- **📅 Extended Forecasts**: View hourly forecasts for today and 5-day predictions with detailed weather metrics.
- **⭐ Favorite Locations**: Save multiple locations for quick access and compare weather across different cities.
- **🔔 Weather Alerts**: Set custom weather alerts with notification or alarm options to stay informed.
- **⚙️ Full Customization**: Choose temperature units (Kelvin, Celsius, Fahrenheit), wind speed units, and language preference.
- **🔐 Cloud Sync**: Synchronized data across devices using Firebase authentication.
- **📶 Offline Access**: Full access to cached weather data and saved favorites without internet connectivity.
- **🎨 Modern UI**: Material Design 3 components with smooth animations and intuitive navigation.

---

## 🛠 Tech Stack & Architecture

### **Architecture**
- **MVVM (Model-View-ViewModel)**: Clean separation of concerns for maintainability and testability.
- **Repository Pattern**: Centralized data management between local and remote sources.

### **Libraries & Tools**
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) for reactive API handling.
- **Database**: [Room Persistence Library](https://developer.android.com/training/data-storage/room) for robust local caching.
- **Images**: [Glide](https://github.com/bumptech/glide) for optimized image loading and caching.
- **Background Tasks**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for periodic data synchronization.
- **Location Services**: [Google Play Services](https://developers.google.com/android/guides/setup) for GPS and Maps integration.

---

## 🏗 Project Structure

```text
com.example.rasd
├── ui
│   ├── home           # Dashboard & current weather display
│   ├── settings       # Location & preference configuration
│   ├── alerts         # Weather alerts management
│   ├── favorites      # Saved locations list
│   └── MainActivity   # Navigation & main activity
├── data
│   ├── local          # Room database & DAOs
│   ├── remote         # Retrofit API service
│   └── repository     # Data abstraction layer
├── utils              # Helpers & extensions
├── services           # Background services & workers

```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio 2022.1+
- Android SDK 13 (API 33)+
- OpenWeatherMap API Key

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/MahmoudRafaat/Weather-Forecast-Application.git
   cd Weather-Forecast-Application
   ```

2. **Get your API Key:**
   - Visit [OpenWeatherMap](https://openweathermap.org/api)
   - Create an account and generate your API key

3. **Add API Key:**
   - Create `local.properties` file in project root
   - Add: `OPENWEATHER_API_KEY=your_api_key_here`

4. **Build & Run:**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

---

## 📖 How to Use

- **Home Screen**: View current weather and today's forecast at a glance.
- **Settings**: Configure location preferences, units, and language.
- **Favorites**: Add and manage multiple favorite locations.
- **Alerts**: Create custom weather alerts with your preferred notification style.
- **Maps**: Interactive location selection with search functionality.

---

## 🧪 Testing
USED junit4 for tesing
```bash
# Unit Tests
./gradlew test

# Instrumented Tests
./gradlew connectedAndroidTest
```



---

Developed with ❤️ by [Mahmoud Rafaat](https://github.com/MahmoudRafaat)
