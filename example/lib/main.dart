import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:sts_one_pay_example/screens/home.dart';

import 'helper/shared_preferences.dart';
import 'one_pay_provider.dart';

void main() async{
  WidgetsFlutterBinding.ensureInitialized();
  //await SharedPreferencesApp.init();
  runApp(const ExampleStsOnePay());
}

class ExampleStsOnePay extends StatelessWidget {
  const ExampleStsOnePay({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => PayOneProvider(),
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        theme: ThemeData.light().copyWith(
          primaryColor: const Color(0xff007A8A),
          scaffoldBackgroundColor: Colors.white,
          colorScheme: ThemeData.light().colorScheme.copyWith(
                secondary: const Color(0xff00A4B4),
              ),
          appBarTheme: const AppBarTheme(
            elevation: 0,
            centerTitle: true,
            titleTextStyle: TextStyle(
              fontSize: 24,
            ),
            backgroundColor: Color(0xff007A8A),
            surfaceTintColor: Colors.transparent,
            scrolledUnderElevation: 0,
            systemOverlayStyle: SystemUiOverlayStyle(
              statusBarColor: Colors.transparent,
              statusBarIconBrightness: Brightness.light,
              statusBarBrightness: Brightness.light,
            ),
          ),
          outlinedButtonTheme: OutlinedButtonThemeData(
            style: ButtonStyle(
              textStyle: MaterialStateProperty.all(
                const TextStyle(
                  fontWeight: FontWeight.w600,
                ),
              ),
              foregroundColor:
                  MaterialStateProperty.all(const Color(0xff007A8A)),
              shape: MaterialStateProperty.all(
                RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(15),
                ),
              ),
              overlayColor: MaterialStateProperty.all(const Color(0xff007A8A)),
              side: MaterialStateProperty.all(
                const BorderSide(
                  width: 2,
                  color: Color(0xff00A4B4),
                ),
              ),
            ),
          ),
          elevatedButtonTheme: ElevatedButtonThemeData(
            style: ButtonStyle(
              backgroundColor:
                  MaterialStateProperty.all(const Color(0xff007A8A)),
              shape: MaterialStateProperty.all(
                RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(15),
                ),
              ),
              overlayColor: MaterialStateProperty.all(const Color(0xff007A8A)),
              textStyle: MaterialStateProperty.all(
                const TextStyle(
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ),
          dropdownMenuTheme: const DropdownMenuThemeData(
            textStyle: TextStyle(color: Colors.black),
          ),
        ),
        home: const HomeStsOnePayExample(),
      ),
    );
  }
}
