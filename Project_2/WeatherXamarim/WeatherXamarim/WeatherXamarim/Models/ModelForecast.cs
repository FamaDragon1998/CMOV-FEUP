﻿using System;
using System.Collections.Generic;
using System.Text;

namespace WeatherApp.Models

{
    public class MainForecast
    {
        public double temp { get; set; }
        public double temp_min { get; set; }
        public double temp_max { get; set; }
        public int pressure { get; set; }
        public int sea_level { get; set; }
        public int grnd_level { get; set; }
        public int humidity { get; set; }
        public double temp_kf { get; set; }

        public string TempDifference
        {

            get
            {
                return string.Format("{0} / {1}", temp_max.ToString("0"), temp_min.ToString("0"));
            }
        }

        public string tempRound
        {

            get
            {
                return Math.Round(temp).ToString("0.0");
            }
        }
    }


    public class RainForecast
    {
        public double __invalid_name__3h { get; set; }
    }


    public class List
    {
        public int dt { get; set; }
        public MainForecast main { get; set; }
        public List<Weather> weather { get; set; }
        public Clouds clouds { get; set; }
        public Wind wind { get; set; }
        public RainForecast rain { get; set; }
        public Sys sys { get; set; }
        public string dt_txt { get; set; }

        public string Date
        {

            get
            {
                DateTime date = Convert.ToDateTime(dt_txt);
                return date.Hour.ToString() + ":00";
            }
        }
    }

   

    public class City
    {
        public int id { get; set; }
        public string name { get; set; }
        public Coord coord { get; set; }
        public string country { get; set; }
        public int population { get; set; }
        public int timezone { get; set; }
        public int sunrise { get; set; }
        public int sunset { get; set; }
    }

    public class RootObjectForecast
    {
        public string cod { get; set; }
        public int message { get; set; }
        public int cnt { get; set; }
        public List<List> list { get; set; }
        public City city { get; set; }
    }

    public class WeatherList
    {
        public List<Weather> allWeathers { get; set; }
    }

    public class SysForecast
    {
        public string pod { get; set; }
    }


}
