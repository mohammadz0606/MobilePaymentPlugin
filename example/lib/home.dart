import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'one_pay_provider.dart';
import 'widgets/payment_fields.dart';

class HomeStsOnePayExample extends StatelessWidget {
  const HomeStsOnePayExample({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("StsOnePayDemo"),
      ),
      body: Column(
        children: [
          Image.asset(
            'assets/images/ic_launcher-playstore.png',
            height: 100,
          ),
          const Expanded(
            child: SingleChildScrollView(
              padding: EdgeInsets.symmetric(horizontal: 15),
              physics: ClampingScrollPhysics(),
              child: PaymentFields(),
            ),
          ),
          Container(
            width: double.infinity,
            padding: const EdgeInsets.symmetric(
              horizontal: 47,
              vertical: 27,
            ),
            child: Consumer<PayOneProvider>(
              builder: (context, provider, child) {
                return Column(
                  children: [
                    SizedBox(
                      width: double.infinity,
                      child: OutlinedButton(
                        onPressed: () async {
                          await provider.openPaymentPage();
                        },
                        child: const Text('Pay Now'),
                      ),
                    ),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton(
                        onPressed: () {},
                        child: const Text('Other API'),
                      ),
                    ),
                  ],
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

/*SizedBox(
            height: MediaQuery.of(context).size.height * 0.09,
            width: double.infinity,
            child: Card(
              margin: EdgeInsets.zero,
              child: Image.asset(
                'assets/images/ic_launcher-playstore.png',
                fit: BoxFit.fitHeight,
                height: 300,
              ),
            ),
          ),*/
