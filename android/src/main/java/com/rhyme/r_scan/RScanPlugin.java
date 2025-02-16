package com.rhyme.r_scan;


import android.app.Activity;

import androidx.annotation.NonNull;

import com.rhyme.r_scan.RScanCamera.RScanPermissions;

import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.platform.PlatformViewRegistry;
import io.flutter.view.TextureRegistry;

public class RScanPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodCallHandlerImpl methodCallHandler;
    private FlutterPluginBinding flutterPluginBinding;
    private MethodChannel channel;

//    public static void registerWith(Registrar registrar) {
//        RScanPlugin plugin = new RScanPlugin();
//        plugin.maybeStartListening(
//                registrar.activity(),
//                registrar.messenger(),
//                registrar::addRequestPermissionsResultListener,
//                registrar.view(), registrar.platformViewRegistry());
//    }

    private void maybeStartListening(
            Activity activity,
            BinaryMessenger messenger,
            RScanPermissions.PermissionsRegistry permissionsRegistry,
            TextureRegistry textureRegistry, PlatformViewRegistry platformViewRegistry) {
        methodCallHandler =
                new MethodCallHandlerImpl(
                        activity, messenger, new RScanPermissions(), permissionsRegistry, textureRegistry, platformViewRegistry);
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.flutterPluginBinding = flutterPluginBinding;
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "r_scan");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        result.notImplemented();
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        this.flutterPluginBinding = null;
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        maybeStartListening(
                binding.getActivity(),
                flutterPluginBinding.getBinaryMessenger(),
                binding::addRequestPermissionsResultListener,
                flutterPluginBinding.getTextureRegistry(),
                flutterPluginBinding.getPlatformViewRegistry());
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        if (methodCallHandler == null) {
            // Could be on too low of an SDK to have started listening originally.
            return;
        }
        methodCallHandler.stopListening();
        methodCallHandler = null;
    }
}
