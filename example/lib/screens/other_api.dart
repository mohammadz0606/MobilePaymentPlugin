import 'dart:io';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../helper/dialogs.dart';
import '../one_pay_provider.dart';
import '../widgets/other_api_fields.dart';

class OtherAPIStsOnePayExample extends StatelessWidget {
  const OtherAPIStsOnePayExample({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Other API'),
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
              child: OtherApiFields(),
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
                          await provider.completion(
                            onError: (code, error) {
                              showCustomDialog(
                                context,
                                title: 'Code:$code',
                                description: error,
                              );
                            },
                          );
                        },
                        child: const Text('Completion'),
                      ),
                    ),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton(
                        onPressed: () async {
                          await provider.refund(
                            onError: (code, error) {
                              showCustomDialog(
                                context,
                                title: 'Code:$code',
                                description: error,
                              );
                            },
                          );
                        },
                        child: const Text('Refund'),
                      ),
                    ),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton(
                        onPressed: () async {
                          await provider.inquiry(
                            onError: (code, error) {
                              showCustomDialog(
                                context,
                                title: 'Code:$code',
                                description: error,
                              );
                            },
                          );
                        },
                        child: const Text('Inquiry'),
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
