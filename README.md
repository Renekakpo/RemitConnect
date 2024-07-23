RemitConnect - Simple Remittance App
============================================

Overview
-------------------------------------------
Welcome to RemitConnect! This app simplifies the process of sending money from Europe to Africa, providing a user-friendly interface for transferring funds to mobile wallets. The app features real-time currency conversion, transparent transaction fees, and a seamless user experience.

Design Decisions
-------------------------------------------
To align closely with the company's app design and to save development time, I utilized the Figma design provided in the assessment. This design closely matches the company’s app aesthetics and provides a solid foundation for creating a user-friendly remittance app.

Architecture
-------------------------------------------
* MVVM (Model-View-ViewModel): Used for clear separation of concerns, making the codebase more manageable and testable.
* Jetpack Compose: Employed for modern UI development, ensuring a responsive and visually appealing interface.
* Retrofit: Utilized for network operations to fetch real-time currency conversion rates and handle API interactions.
* LiveData: Used for observing data changes and updating the UI accordingly.


Core Features
-------------------------------------------
1. Send Money Flow: Users can select a destination country, choose a recipient, select a mobile wallet, and specify the amount to send.
2. Real-Time Currency Conversion: Conversion rates are fetched from the provided API, and the received amount is displayed instantly.
3. Transaction Details: The app shows conversion rates and transaction fees, ensuring transparency.
4. Transaction Summary: A confirmation screen displays a summary of the transaction, including details such as total amount, recipient, mobile wallet, conversion rate, and fees.

Challenges Faced
-------------------------------------------
1. Real-Time Conversion Rate Handling: Implementing real-time currency conversion required careful integration with the Retrofit library and handling potential network errors. Unfortunately the api does not provide the endpoint to fetch the conversion rate.
2. UI Responsiveness: Ensuring the UI remains responsive and user-friendly while fetching data in real time posed a challenge. Jetpack Compose's state management helped address this issue effectively.
3. Error Handling: Providing meaningful error messages and ensuring graceful error handling, especially for network-related issues, required thorough testing and validation.

Additional Notes
-------------------------------------------
* Design: The Figma design provided in the assessment was followed closely to align with the company's app design and ensure consistency.
* Testing: The app has been tested for core functionalities, but further testing on various devices and network conditions is recommended.
* Documentation: The source code is well-commented to facilitate understanding and future modifications.

Build and Run Instructions
-------------------------------------------
1. Clone the Repository:
`git clone https://github.com/your-username/remitconnect.git`
2. Navigate to the Project Directory:
`cd remitconnect`
3. Open in Android Studio:
4. Open the project in Android Studio and sync the Gradle files.

Build and Run:
-------------------------------------------
Select your desired emulator or connected device and run the app.

Contact
-------------------------------------------
For any questions or clarifications, feel free to reach out to me. I hope you find the app meets your expectations and look forward to your feedback.