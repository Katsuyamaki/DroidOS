/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /data/data/com.termux/files/home/android-sdk/build-tools/35.0.0/aidl -p/data/data/com.termux/files/home/android-sdk/platforms/android-35/framework.aidl -o/data/data/com.termux/files/home/projects/DroidOS/DroidOSKeyboardTrackpad/app/build/generated/aidl_source_output_dir/debug/out -I/data/data/com.termux/files/home/projects/DroidOS/DroidOSKeyboardTrackpad/app/src/main/aidl -I/data/data/com.termux/files/home/projects/DroidOS/DroidOSKeyboardTrackpad/app/src/debug/aidl -I/data/data/com.termux/files/home/.gradle/caches/8.13/transforms/0191a4d215e2716b5fc94116d888225b/transformed/core-1.13.1/aidl -I/data/data/com.termux/files/home/.gradle/caches/8.13/transforms/4c5ba2ab8ae42e8eecfa40ccd9284b6e/transformed/versionedparcelable-1.1.1/aidl -d/data/data/com.termux/files/usr/tmp/aidl6001301020718652883.d /data/data/com.termux/files/home/projects/DroidOS/DroidOSKeyboardTrackpad/app/src/main/aidl/com/katsuyamaki/DroidOSTrackpadKeyboard/IShellService.aidl
 */
package com.katsuyamaki.DroidOSTrackpadKeyboard;
public interface IShellService extends android.os.IInterface
{
  /** Default implementation for IShellService. */
  public static class Default implements com.katsuyamaki.DroidOSTrackpadKeyboard.IShellService
  {
    @Override public void injectMouse(int action, float x, float y, int displayId, int source, int buttonState, long downTime) throws android.os.RemoteException
    {
    }
    @Override public void injectScroll(float x, float y, float vDistance, float hDistance, int displayId) throws android.os.RemoteException
    {
    }
    @Override public void execClick(float x, float y, int displayId) throws android.os.RemoteException
    {
    }
    @Override public void execRightClick(float x, float y, int displayId) throws android.os.RemoteException
    {
    }
    // Updated to support deviceId parameter
    @Override public void injectKey(int keyCode, int action, int metaState, int displayId, int deviceId) throws android.os.RemoteException
    {
    }
    // NEW: Trigger for "Hardware Keyboard" detection
    @Override public void injectDummyHardwareKey(int displayId) throws android.os.RemoteException
    {
    }
    @Override public void setWindowingMode(int taskId, int mode) throws android.os.RemoteException
    {
    }
    @Override public void resizeTask(int taskId, int left, int top, int right, int bottom) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String runCommand(java.lang.String cmd) throws android.os.RemoteException
    {
      return null;
    }
    // Screen Control Methods
    @Override public void setScreenOff(int displayIndex, boolean turnOff) throws android.os.RemoteException
    {
    }
    @Override public void setBrightness(int value) throws android.os.RemoteException
    {
    }
    @Override public boolean setBrightnessViaDisplayManager(int displayId, float brightness) throws android.os.RemoteException
    {
      return false;
    }
    @Override public void setSystemCursorVisibility(boolean visible) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.katsuyamaki.DroidOSTrackpadKeyboard.IShellService
  {
    /** Construct the stub at attach it to the interface. */
    @SuppressWarnings("this-escape")
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.katsuyamaki.DroidOSTrackpadKeyboard.IShellService interface,
     * generating a proxy if needed.
     */
    public static com.katsuyamaki.DroidOSTrackpadKeyboard.IShellService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.katsuyamaki.DroidOSTrackpadKeyboard.IShellService))) {
        return ((com.katsuyamaki.DroidOSTrackpadKeyboard.IShellService)iin);
      }
      return new com.katsuyamaki.DroidOSTrackpadKeyboard.IShellService.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      if (code == INTERFACE_TRANSACTION) {
        reply.writeString(descriptor);
        return true;
      }
      switch (code)
      {
        case TRANSACTION_injectMouse:
        {
          int _arg0;
          _arg0 = data.readInt();
          float _arg1;
          _arg1 = data.readFloat();
          float _arg2;
          _arg2 = data.readFloat();
          int _arg3;
          _arg3 = data.readInt();
          int _arg4;
          _arg4 = data.readInt();
          int _arg5;
          _arg5 = data.readInt();
          long _arg6;
          _arg6 = data.readLong();
          this.injectMouse(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_injectScroll:
        {
          float _arg0;
          _arg0 = data.readFloat();
          float _arg1;
          _arg1 = data.readFloat();
          float _arg2;
          _arg2 = data.readFloat();
          float _arg3;
          _arg3 = data.readFloat();
          int _arg4;
          _arg4 = data.readInt();
          this.injectScroll(_arg0, _arg1, _arg2, _arg3, _arg4);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_execClick:
        {
          float _arg0;
          _arg0 = data.readFloat();
          float _arg1;
          _arg1 = data.readFloat();
          int _arg2;
          _arg2 = data.readInt();
          this.execClick(_arg0, _arg1, _arg2);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_execRightClick:
        {
          float _arg0;
          _arg0 = data.readFloat();
          float _arg1;
          _arg1 = data.readFloat();
          int _arg2;
          _arg2 = data.readInt();
          this.execRightClick(_arg0, _arg1, _arg2);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_injectKey:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          int _arg2;
          _arg2 = data.readInt();
          int _arg3;
          _arg3 = data.readInt();
          int _arg4;
          _arg4 = data.readInt();
          this.injectKey(_arg0, _arg1, _arg2, _arg3, _arg4);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_injectDummyHardwareKey:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.injectDummyHardwareKey(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_setWindowingMode:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          this.setWindowingMode(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_resizeTask:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          int _arg2;
          _arg2 = data.readInt();
          int _arg3;
          _arg3 = data.readInt();
          int _arg4;
          _arg4 = data.readInt();
          this.resizeTask(_arg0, _arg1, _arg2, _arg3, _arg4);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_runCommand:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.runCommand(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_setScreenOff:
        {
          int _arg0;
          _arg0 = data.readInt();
          boolean _arg1;
          _arg1 = (0!=data.readInt());
          this.setScreenOff(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_setBrightness:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.setBrightness(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_setBrightnessViaDisplayManager:
        {
          int _arg0;
          _arg0 = data.readInt();
          float _arg1;
          _arg1 = data.readFloat();
          boolean _result = this.setBrightnessViaDisplayManager(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_setSystemCursorVisibility:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setSystemCursorVisibility(_arg0);
          reply.writeNoException();
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements com.katsuyamaki.DroidOSTrackpadKeyboard.IShellService
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public void injectMouse(int action, float x, float y, int displayId, int source, int buttonState, long downTime) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(action);
          _data.writeFloat(x);
          _data.writeFloat(y);
          _data.writeInt(displayId);
          _data.writeInt(source);
          _data.writeInt(buttonState);
          _data.writeLong(downTime);
          boolean _status = mRemote.transact(Stub.TRANSACTION_injectMouse, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void injectScroll(float x, float y, float vDistance, float hDistance, int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeFloat(x);
          _data.writeFloat(y);
          _data.writeFloat(vDistance);
          _data.writeFloat(hDistance);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_injectScroll, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void execClick(float x, float y, int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeFloat(x);
          _data.writeFloat(y);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_execClick, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void execRightClick(float x, float y, int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeFloat(x);
          _data.writeFloat(y);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_execRightClick, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // Updated to support deviceId parameter
      @Override public void injectKey(int keyCode, int action, int metaState, int displayId, int deviceId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(keyCode);
          _data.writeInt(action);
          _data.writeInt(metaState);
          _data.writeInt(displayId);
          _data.writeInt(deviceId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_injectKey, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // NEW: Trigger for "Hardware Keyboard" detection
      @Override public void injectDummyHardwareKey(int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_injectDummyHardwareKey, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setWindowingMode(int taskId, int mode) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(taskId);
          _data.writeInt(mode);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setWindowingMode, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void resizeTask(int taskId, int left, int top, int right, int bottom) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(taskId);
          _data.writeInt(left);
          _data.writeInt(top);
          _data.writeInt(right);
          _data.writeInt(bottom);
          boolean _status = mRemote.transact(Stub.TRANSACTION_resizeTask, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.lang.String runCommand(java.lang.String cmd) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(cmd);
          boolean _status = mRemote.transact(Stub.TRANSACTION_runCommand, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Screen Control Methods
      @Override public void setScreenOff(int displayIndex, boolean turnOff) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(displayIndex);
          _data.writeInt(((turnOff)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setScreenOff, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setBrightness(int value) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(value);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setBrightness, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean setBrightnessViaDisplayManager(int displayId, float brightness) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(displayId);
          _data.writeFloat(brightness);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setBrightnessViaDisplayManager, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setSystemCursorVisibility(boolean visible) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((visible)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setSystemCursorVisibility, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
    }
    static final int TRANSACTION_injectMouse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_injectScroll = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_execClick = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_execRightClick = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_injectKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_injectDummyHardwareKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_setWindowingMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_resizeTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_runCommand = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_setScreenOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_setBrightness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_setBrightnessViaDisplayManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_setSystemCursorVisibility = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
  }
  /** @hide */
  public static final java.lang.String DESCRIPTOR = "com.katsuyamaki.DroidOSTrackpadKeyboard.IShellService";
  public void injectMouse(int action, float x, float y, int displayId, int source, int buttonState, long downTime) throws android.os.RemoteException;
  public void injectScroll(float x, float y, float vDistance, float hDistance, int displayId) throws android.os.RemoteException;
  public void execClick(float x, float y, int displayId) throws android.os.RemoteException;
  public void execRightClick(float x, float y, int displayId) throws android.os.RemoteException;
  // Updated to support deviceId parameter
  public void injectKey(int keyCode, int action, int metaState, int displayId, int deviceId) throws android.os.RemoteException;
  // NEW: Trigger for "Hardware Keyboard" detection
  public void injectDummyHardwareKey(int displayId) throws android.os.RemoteException;
  public void setWindowingMode(int taskId, int mode) throws android.os.RemoteException;
  public void resizeTask(int taskId, int left, int top, int right, int bottom) throws android.os.RemoteException;
  public java.lang.String runCommand(java.lang.String cmd) throws android.os.RemoteException;
  // Screen Control Methods
  public void setScreenOff(int displayIndex, boolean turnOff) throws android.os.RemoteException;
  public void setBrightness(int value) throws android.os.RemoteException;
  public boolean setBrightnessViaDisplayManager(int displayId, float brightness) throws android.os.RemoteException;
  public void setSystemCursorVisibility(boolean visible) throws android.os.RemoteException;
}
