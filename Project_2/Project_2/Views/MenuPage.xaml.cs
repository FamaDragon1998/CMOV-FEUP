using Project_2.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Project_2.Views
{
    // Learn more about making custom code visible in the Xamarin.Forms previewer
    // by visiting https://aka.ms/xamarinforms-previewer
    [DesignTimeVisible(false)]
    public partial class MenuPage : ContentPage
    {
        MainPage RootPage { get => Application.Current.MainPage as MainPage; }
        List<HomeMenuItem> selectedCities;

        public MenuPage()
        {
            InitializeComponent();


            selectedCities = new List<HomeMenuItem>
            {
                new HomeMenuItem {Id = MenuItemType.Browse, Title="Porto" },
                new HomeMenuItem {Id = MenuItemType.About, Title="Lisboa" },

            };

            ListViewMenu.ItemsSource = selectedCities;

            ListViewMenu.SelectedItem = selectedCities[0];

            ListViewMenu.ItemSelected += async (sender, e) =>
            {
                if (e.SelectedItem == null)
                    return;

                var id = (int)((HomeMenuItem)e.SelectedItem).Id;
                await RootPage.NavigateFromMenu(id);
            };
        }

        private void AddCityButton(object sender, EventArgs e)
        {

        }
    }

}