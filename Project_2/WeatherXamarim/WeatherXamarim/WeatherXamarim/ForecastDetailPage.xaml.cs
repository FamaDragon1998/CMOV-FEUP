using System.Diagnostics;
using WeatherApp.Models;
using WeatherXamarim.Models;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace WeatherXamarim
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class ForecastDetailPage : ContentPage
    {
        
        public ForecastDetailPage(List info)
        {
            
            InitializeComponent();
            Title = info.Date + " Forecast";
            WeatherBackground.Source = info.weather[0].IconUrl;
            temperature.Text = info.main.temp.ToString();
           /* Humidity.Text = info.main.humidity.ToString() + "%";
            Clouds.Text = info.clouds.all.ToString() + "%";
            Wind_Speed.Text = info.wind.speed.ToString() + " m/s";
            Wind_Deg.Text = info.wind.deg.ToString() + "º";
            Description.Text = Utils.FirstCharToUpper(info.weather[0].description);
            //SetBackgroundImage(root.weather[0].description);
            if (info.rain != null)
            {
                if (info.rain.__invalid_name__3h > 0)
                    Description.Text += " (" + info.rain.__invalid_name__3h.ToString() + " mm)";
            }*/
        }
    }
}