using System;
using System.Linq;

namespace WeatherXamarim.Models
{
    public static class Utils
    {
        public static string FirstCharToUpper(this string input)
        {
            switch (input)
            {
                case null: throw new ArgumentNullException(nameof(input));
                case "": throw new ArgumentException($"{nameof(input)} cannot be empty", nameof(input));
                default: return input.First().ToString().ToUpper() + input.Substring(1);
            }
        }

        public static string GetBackgroundImage(string description)
        {
            switch (description)
            {
                case "clear sky":
                    return "sunny.png";
                case "few clouds":
                    return "slightlycloudy.png";
                case "scattered clouds":
                    return "slightlycloudy.png";
                case "thunderstorm":
                    return "zeushatesyou.png";
                default:
                    if (description.Contains("rain"))
                    {
                        return "rainy.png";
                    }
                    else if (description.Contains("clouds"))
                    {
                        return "cloudy.png";
                    }
                    else if (description.Contains("mist"))
                    {
                        return "mist.png";
                    }
                    else if (description.Contains("snow"))
                    {
                        return "snow.png";
                    }
                    else 
                        return "overlay.png";
            }
        }
      
    }
}

