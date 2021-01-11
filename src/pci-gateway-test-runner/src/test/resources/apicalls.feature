# new feature
# Tags: optional

Feature: Api Call Tests

  Scenario Outline: Test Redirect Controller
    Given An api request to be proxied to bambora with path "<path>" and with queryParams: "<queryParams>"
    When Calling PCI Gateway to proxy the request
    Then The response should be <httpstatuscode>
    Examples:
      | path | queryParams | httpstatuscode |
      | /pcigw/paymentProfile/webform.asp | merchantId=1234&test=test | 200 |