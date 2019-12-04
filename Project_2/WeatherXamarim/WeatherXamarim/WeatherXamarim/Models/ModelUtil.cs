using System;
using System.Collections.Generic;
using System.Text;

namespace WeatherApp.Models
{
    public class Coord
    {
        public double lat { get; set; }
        public double lon { get; set; }
    }

    public class Wind
    {
        public double speed { get; set; }
        public int deg { get; set; }
    }

    public class Weather
    {
        public int id { get; set; }
        public string main { get; set; }
        public string description { get; set; }
        public string icon { get; set; }

        public string IconUrl
        {

            get
            {
                return "http://openweathermap.org/img/wn/" + icon + "@2x.png";
            }
        }
    }

}
