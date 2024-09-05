---

# WifiBall Companion App (GyroScope)

This companion Android application is designed to work with the `WifiBall` project, which visualizes real-time WiFi data in a 3D environment. The app streams gyroscope data from an Android device to the `WifiBall` server, enabling the accurate positioning of detected WiFi access points in the 3D visualization.

## Features

- **Real-Time Gyroscope Streaming:** Streams gyroscope data from your Android device to the `WifiBall` server, allowing the 3D environment to reflect the current orientation of the WiFi antenna.
- **Network Configuration:** Easily connect the Android app to the `WifiBall` server by specifying the IP address and port.
- **Lightweight and Efficient:** The app is optimized to run efficiently in the background while providing accurate orientation data.

## Prerequisites

- An Android device with a built-in gyroscope sensor.
- Access to a WiFi network that both the Android device and the `WifiBall` server can connect to.
- The `WifiBall` server up and running, ready to receive gyroscope data.

## Installation

1. **Download the App:**
   - Download the latest APK from the [releases section](#) or build the app from source.

2. **Install the App:**
   - On your Android device, navigate to `Settings > Security` and enable `Unknown Sources` to allow the installation of apps from sources other than the Google Play Store.
   - Transfer the APK to your Android device and install it.

## Usage

1. **Launch the App:**
   - Open the WifiBall Companion App on your Android device.

2. **Connect to the WifiBall Server:**
   - Enter the IP address of the computer running the `WifiBall` server.
   - Ensure the default port `8011` is correctly entered (or modify it if the server uses a different port).
   - Tap the "Connect" button to establish the WebSocket connection.

3. **Mount the Device:**
   - Mount your Android device on the directional WiFi antenna, ensuring it is securely attached and aligned with the antenna.

4. **Start Scanning:**
   - The app will automatically begin streaming gyroscope data to the `WifiBall` server.
   - Begin your WiFi scan using the `WifiBall` application on your computer.

5. **Monitor the 3D Visualization:**
   - Open the 3D visualization in your web browser (http://localhost:8011/realtime-data) and observe the real-time positioning of WiFi access points as you move the antenna.

## Troubleshooting

- **Connection Issues:**
  - Ensure that both your Android device and the `WifiBall` server are on the same network.
  - Double-check the IP address and port number.
  - Restart the app and try connecting again if the connection fails.

- **Inaccurate Positioning:**
  - Make sure the Android device is securely mounted and aligned with the WiFi antenna.
  - Move the antenna smoothly and slowly to allow accurate data capture.

## Contributing

If you are an Android developer and would like to contribute to improving this app, feel free to fork the repository and submit a pull request. Contributions are welcome, especially in areas like performance optimization, UI/UX improvements, and additional feature integration.

## License

This project is licensed under the MIT License. See the [LICENSE](#) file for details.

## Contact

For any issues, suggestions, or questions, please open an issue on the main `WifiBall` [repository](https://github.com/emp3r0r7/WifiBall).

---
