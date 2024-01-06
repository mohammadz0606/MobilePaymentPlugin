import 'package:flutter/material.dart';
import 'package:sts_one_pay/models/payment_page_response.dart';

class ResponseDialog extends StatelessWidget {
  const ResponseDialog({
    super.key,
    required this.result,
  });

  final StsOnePayResponse result;

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Response'),
      titleTextStyle: TextStyle(
        color: Theme.of(context).primaryColor,
        fontWeight: FontWeight.w600,
        fontSize: 20,
      ),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (result.token != null) ...{
            _buildText(context,
                title: 'StatusCode', data: result.statusCode.toString()),
            _buildText(context,
                title: 'MessageID', data: result.messageID.toString()),
            _buildText(context,
                title: 'SaveCard', data: result.saveCard.toString()),
            _buildText(context, title: 'rrn', data: result.rrn.toString()),
            _buildText(context,
                title: 'ApprovalCode', data: result.approvalCode.toString()),
            _buildText(context,
                title: 'ApprovalCode',
                data: result.gatewayStatusCode.toString()),
            _buildText(context, title: 'Token', data: result.token.toString()),
            _buildText(context,
                title: 'PaymentMethod', data: result.paymentMethod.toString()),
            _buildText(context,
                title: 'StatusDescription',
                data: result.statusDescription.toString()),
            _buildText(context,
                title: 'SecureHash', data: result.secureHash.toString()),
            _buildText(context,
                title: 'Amount', data: result.amount.toString()),
            _buildText(context,
                title: 'TransactionID', data: result.transactionID.toString()),
            _buildText(context,
                title: 'CardNumber', data: result.cardNumber.toString()),
            _buildText(context,
                title: 'GatewayName', data: result.gatewayName.toString()),
            _buildText(context,
                title: 'CurrencyISOCode',
                data: result.currencyISOCode.toString()),
            _buildText(context,
                title: 'ResponseHashMatch',
                data: result.responseHashMatch.toString()),
          } else ...{
            _buildText(context,
                title: 'SecureHash', data: result.secureHash.toString()),
            _buildText(context,
                title: 'StatusCode', data: result.statusCode.toString()),
            _buildText(context,
                title: 'StatusDescription',
                data: result.statusDescription.toString()),
            _buildText(context,
                title: 'ResponseHashMatch',
                data: result.responseHashMatch.toString()),
          },
        ],
      ),
      actions: [
        TextButton(
          onPressed: () {
            Navigator.of(context).pop();
          },
          style: ButtonStyle(
            foregroundColor:
                MaterialStateProperty.all(Theme.of(context).primaryColor),
          ),
          child: const Text('Ok'),
        ),
      ],
    );
  }

  Text _buildText(
    BuildContext context, {
    required String title,
    required String data,
  }) {
    return Text.rich(
      TextSpan(
        children: [
          TextSpan(
            text: '$title: ',
            style: TextStyle(
              color: Theme.of(context).primaryColor,
            ),
          ),
          TextSpan(text: data),
        ],
      ),
    );
  }
}
