using System.Diagnostics;
using WeatherApp.Models;
using Xamarin.Forms;

namespace WeatherXamarim
{
    internal class DetailForecastPage : ContentPage
    {
        private List info;

        public DetailForecastPage(List info)
        {
            this.info = info;

            Debug.WriteLine("info", info.weather[0].description);
        }
    }
}