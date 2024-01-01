#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint sts_one_pay.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'sts_one_pay'
  s.version          = '0.0.1'
  s.summary          = 'A new Flutter project.'
  s.description      = <<-DESC
A new Flutter project.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.platform = :ios, '11.0'
#     s.source           = { :path => 'https://github.com/CocoaPods/Specs.git' }
#   s.dependency 'MobilePaymentSDK-iOS'
  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
 # s.ios.deployment_target = '10.0'
 # s.preserve_paths = 'MobilePaymentSDK.framework'
 # s.xcconfig = { 'OTHER_LDFLAGS' => '-framework MobilePaymentSDK' }
 # s.vendored_frameworks = 'MobilePaymentSDK.framework'
 s.preserve_paths = 'ThreeDS_SDK.xcframework'
 s.xcconfig = { 'OTHER_LDFLAGS' => '-framework ThreeDS_SDK' }
 s.vendored_frameworks = 'ThreeDS_SDK.xcframework'
 s.public_header_files = 'Classes/**/*.h'
  # s.preserve_paths = 'MobilePaymentSDK.framework'
  # s.xcconfig = { 'OTHER_LDFLAGS' => '-framework MobilePaymentSDK' }
  # s.vendored_frameworks = 'MobilePaymentSDK.framework'
    s.static_framework = true
end
