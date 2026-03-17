package com.example.weather_forecast_app.data.weather.repository.fakemodel

import com.example.weather_forecast_app.data.weather.model.*

object TestDataFactory {

    fun fakeWeatherModel() = WeatherModel(
        base = "stations",
        cod = 200,
        coord = Coord(
            lon = 31.2357,
            lat = 30.0444
        ),
        dt = 1710000000,
        id = 360630,
        name = "Cairo",
        timezone = 7200,
        visibility = 10000,
        clouds = Clouds(all = 20),
        wind = Wind(
            speed = 5.0,
            deg = 180,
            gust = 0.0
        ),
        main = Main(
            temp = 25.0,
            humidity = 60,
            pressure = 1012,
            feels_like = 25.0,
            temp_min = 20.0,
            temp_max = 30.0
        ),
        sys = Sys(
            country = "EG",
            id = 1,
            sunrise = 1709950000,
            sunset = 1710000000,
            type = 1
        ),
        weather = listOf(
            Weather(
                id = 800,
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        )
    )

    fun fakeForecast(): HourlyForecast {
        val item = Item0(
            dt = 1710000000,
            main = Main(
                temp = 26.0,
                humidity = 55,
                pressure = 1010,
                feels_like = 26.0,
                temp_min = 22.0,
                temp_max = 28.0
            ),
            weather = listOf(
                Weather(
                    id = 801,
                    main = "Clouds",
                    description = "few clouds",
                    icon = "02d"
                )
            ),
            clouds = Clouds(all = 10),
            wind = Wind(
                speed = 4.0,
                deg = 150,
                gust = 0.0
            ),
            visibility = 10000,
            pop = 0.0,
            dt_txt = "2024-03-09 12:00:00",
            sys = SysX(pod = "d")
        )

        return HourlyForecast(
            cod = "200",
            message = 0,
            cnt = 10,
            city = City(
                id = 360630,
                name = "Cairo",
                country = "EG",
                timezone = 7200,
                coord = Coord(31.2357, 30.0444),
                population = 10000000,
                sunrise = 1709950000,
                sunset = 1710000000
            ),
            list = List(10) { item }
        )
    }
}
