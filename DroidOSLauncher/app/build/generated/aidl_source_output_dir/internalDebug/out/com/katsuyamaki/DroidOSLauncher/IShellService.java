/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /data/data/com.termux/files/home/android-sdk/build-tools/35.0.0/aidl -p/data/data/com.termux/files/home/android-sdk/platforms/android-35/framework.aidl -o/data/data/com.termux/files/home/projects/DroidOS/DroidOSLauncher/app/build/generated/aidl_source_output_dir/internalDebug/out -I/data/data/com.termux/files/home/projects/DroidOS/DroidOSLauncher/app/src/main/aidl -I/data/data/com.termux/files/home/projects/DroidOS/DroidOSLauncher/app/src/internal/aidl -I/data/data/com.termux/files/home/projects/DroidOS/DroidOSLauncher/app/src/debug/aidl -I/data/data/com.termux/files/home/projects/DroidOS/DroidOSLauncher/app/src/internalDebug/aidl -I/data/data/com.termux/files/home/.gradle/caches/8.13/transforms/e0a624629211807dd212ae4665044dcb/transformed/core-1.10.1/aidl -I/data/data/com.termux/files/home/.gradle/caches/8.13/transforms/c45227c528e58b9cb82b07e063a1e17c/transformed/versionedparcelable-1.1.1/aidl -d/data/data/com.termux/files/usr/tmp/aidl9771616519663615059.d /data/data/com.termux/files/home/projects/DroidOS/DroidOSLauncher/app/src/main/aidl/com/katsuyamaki/DroidOSLauncher/IShellService.aidl
 */
package com.katsuyamaki.DroidOSLauncher;
public interface IShellService extends android.os.IInterface
{
  /** Default implementation for IShellService. */
  public static class Default implements com.katsuyamaki.DroidOSLauncher.IShellService
  {
    @Override public void injectKey(int keyCode, int action, int flags, int displayId, int metaState) throws android.os.RemoteException
    {
    }
    @Override public void forceStop(java.lang.String packageName) throws android.os.RemoteException
    {
    }
    @Override public void runCommand(java.lang.String command) throws android.os.RemoteException
    {
    }
    @Override public void setScreenOff(int displayIndex, boolean turnOff) throws android.os.RemoteException
    {
    }
    @Override public void repositionTask(java.lang.String packageName, java.lang.String className, int left, int top, int right, int bottom) throws android.os.RemoteException
    {
    }
    @Override public java.util.List<java.lang.String> getVisiblePackages(int displayId) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<java.lang.String> getVisibleComponents(int displayId) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<java.lang.String> getVisibleTaskComponents(int displayId) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<java.lang.String> getWindowLayouts(int displayId) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.util.List<java.lang.String> getAllRunningPackages() throws android.os.RemoteException
    {
      return null;
    }
    @Override public int getTaskId(java.lang.String packageName, java.lang.String className) throws android.os.RemoteException
    {
      return 0;
    }
    @Override public void moveTaskToBack(int taskId) throws android.os.RemoteException
    {
    }
    @Override public void moveTaskToFront(int taskId, int displayId) throws android.os.RemoteException
    {
    }
    @Override public void setFocusedTask(int taskId) throws android.os.RemoteException
    {
    }
    @Override public boolean restoreMinimizedTask(int taskId, int displayId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean moveTaskToDisplay(int taskId, int displayId) throws android.os.RemoteException
    {
      return false;
    }
    @Override public java.lang.String dumpMultiTaskingMethods() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void batchResize(java.util.List<java.lang.String> packages, int[] bounds) throws android.os.RemoteException
    {
    }
    @Override public void batchResizeComponents(java.util.List<java.lang.String> packages, java.util.List<java.lang.String> classes, int[] bounds) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String getTaskDebugSnapshot(java.lang.String packageName) throws android.os.RemoteException
    {
      return null;
    }
    @Override public boolean isTaskFreeform(java.lang.String packageName) throws android.os.RemoteException
    {
      return false;
    }
    // Brightness Control
    @Override public void setSystemBrightness(int brightness) throws android.os.RemoteException
    {
    }
    @Override public int getSystemBrightness() throws android.os.RemoteException
    {
      return 0;
    }
    @Override public float getSystemBrightnessFloat() throws android.os.RemoteException
    {
      return 0.0f;
    }
    @Override public void setAutoBrightness(boolean enabled) throws android.os.RemoteException
    {
    }
    @Override public boolean isAutoBrightness() throws android.os.RemoteException
    {
      return false;
    }
    // Legacy / Direct Hardware Control
    @Override public boolean setBrightnessViaDisplayManager(int displayId, float brightness) throws android.os.RemoteException
    {
      return false;
    }
    // NEW: Alternate Display Off Logic (Targeted)
    @Override public void setBrightness(int displayId, int value) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.katsuyamaki.DroidOSLauncher.IShellService
  {
    /** Construct the stub at attach it to the interface. */
    @SuppressWarnings("this-escape")
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.katsuyamaki.DroidOSLauncher.IShellService interface,
     * generating a proxy if needed.
     */
    public static com.katsuyamaki.DroidOSLauncher.IShellService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.katsuyamaki.DroidOSLauncher.IShellService))) {
        return ((com.katsuyamaki.DroidOSLauncher.IShellService)iin);
      }
      return new com.katsuyamaki.DroidOSLauncher.IShellService.Stub.Proxy(obj);
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
        case TRANSACTION_forceStop:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.forceStop(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_runCommand:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.runCommand(_arg0);
          reply.writeNoException();
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
        case TRANSACTION_repositionTask:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          int _arg2;
          _arg2 = data.readInt();
          int _arg3;
          _arg3 = data.readInt();
          int _arg4;
          _arg4 = data.readInt();
          int _arg5;
          _arg5 = data.readInt();
          this.repositionTask(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getVisiblePackages:
        {
          int _arg0;
          _arg0 = data.readInt();
          java.util.List<java.lang.String> _result = this.getVisiblePackages(_arg0);
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_getVisibleComponents:
        {
          int _arg0;
          _arg0 = data.readInt();
          java.util.List<java.lang.String> _result = this.getVisibleComponents(_arg0);
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_getVisibleTaskComponents:
        {
          int _arg0;
          _arg0 = data.readInt();
          java.util.List<java.lang.String> _result = this.getVisibleTaskComponents(_arg0);
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_getWindowLayouts:
        {
          int _arg0;
          _arg0 = data.readInt();
          java.util.List<java.lang.String> _result = this.getWindowLayouts(_arg0);
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_getAllRunningPackages:
        {
          java.util.List<java.lang.String> _result = this.getAllRunningPackages();
          reply.writeNoException();
          reply.writeStringList(_result);
          break;
        }
        case TRANSACTION_getTaskId:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          int _result = this.getTaskId(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_moveTaskToBack:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.moveTaskToBack(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_moveTaskToFront:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          this.moveTaskToFront(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_setFocusedTask:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.setFocusedTask(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_restoreMinimizedTask:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          boolean _result = this.restoreMinimizedTask(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_moveTaskToDisplay:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          boolean _result = this.moveTaskToDisplay(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_dumpMultiTaskingMethods:
        {
          java.lang.String _result = this.dumpMultiTaskingMethods();
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_batchResize:
        {
          java.util.List<java.lang.String> _arg0;
          _arg0 = data.createStringArrayList();
          int[] _arg1;
          _arg1 = data.createIntArray();
          this.batchResize(_arg0, _arg1);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_batchResizeComponents:
        {
          java.util.List<java.lang.String> _arg0;
          _arg0 = data.createStringArrayList();
          java.util.List<java.lang.String> _arg1;
          _arg1 = data.createStringArrayList();
          int[] _arg2;
          _arg2 = data.createIntArray();
          this.batchResizeComponents(_arg0, _arg1, _arg2);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getTaskDebugSnapshot:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.getTaskDebugSnapshot(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          break;
        }
        case TRANSACTION_isTaskFreeform:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          boolean _result = this.isTaskFreeform(_arg0);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          break;
        }
        case TRANSACTION_setSystemBrightness:
        {
          int _arg0;
          _arg0 = data.readInt();
          this.setSystemBrightness(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_getSystemBrightness:
        {
          int _result = this.getSystemBrightness();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getSystemBrightnessFloat:
        {
          float _result = this.getSystemBrightnessFloat();
          reply.writeNoException();
          reply.writeFloat(_result);
          break;
        }
        case TRANSACTION_setAutoBrightness:
        {
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.setAutoBrightness(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_isAutoBrightness:
        {
          boolean _result = this.isAutoBrightness();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
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
        case TRANSACTION_setBrightness:
        {
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          this.setBrightness(_arg0, _arg1);
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
    private static class Proxy implements com.katsuyamaki.DroidOSLauncher.IShellService
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
      @Override public void injectKey(int keyCode, int action, int flags, int displayId, int metaState) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(keyCode);
          _data.writeInt(action);
          _data.writeInt(flags);
          _data.writeInt(displayId);
          _data.writeInt(metaState);
          boolean _status = mRemote.transact(Stub.TRANSACTION_injectKey, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void forceStop(java.lang.String packageName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_forceStop, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void runCommand(java.lang.String command) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(command);
          boolean _status = mRemote.transact(Stub.TRANSACTION_runCommand, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
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
      @Override public void repositionTask(java.lang.String packageName, java.lang.String className, int left, int top, int right, int bottom) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeString(className);
          _data.writeInt(left);
          _data.writeInt(top);
          _data.writeInt(right);
          _data.writeInt(bottom);
          boolean _status = mRemote.transact(Stub.TRANSACTION_repositionTask, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.util.List<java.lang.String> getVisiblePackages(int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getVisiblePackages, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<java.lang.String> getVisibleComponents(int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getVisibleComponents, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<java.lang.String> getVisibleTaskComponents(int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getVisibleTaskComponents, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<java.lang.String> getWindowLayouts(int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getWindowLayouts, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.util.List<java.lang.String> getAllRunningPackages() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.util.List<java.lang.String> _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAllRunningPackages, _data, _reply, 0);
          _reply.readException();
          _result = _reply.createStringArrayList();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public int getTaskId(java.lang.String packageName, java.lang.String className) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeString(className);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getTaskId, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void moveTaskToBack(int taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_moveTaskToBack, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void moveTaskToFront(int taskId, int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(taskId);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_moveTaskToFront, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setFocusedTask(int taskId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(taskId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setFocusedTask, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean restoreMinimizedTask(int taskId, int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(taskId);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_restoreMinimizedTask, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean moveTaskToDisplay(int taskId, int displayId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(taskId);
          _data.writeInt(displayId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_moveTaskToDisplay, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String dumpMultiTaskingMethods() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_dumpMultiTaskingMethods, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void batchResize(java.util.List<java.lang.String> packages, int[] bounds) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStringList(packages);
          _data.writeIntArray(bounds);
          boolean _status = mRemote.transact(Stub.TRANSACTION_batchResize, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void batchResizeComponents(java.util.List<java.lang.String> packages, java.util.List<java.lang.String> classes, int[] bounds) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStringList(packages);
          _data.writeStringList(classes);
          _data.writeIntArray(bounds);
          boolean _status = mRemote.transact(Stub.TRANSACTION_batchResizeComponents, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.lang.String getTaskDebugSnapshot(java.lang.String packageName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getTaskDebugSnapshot, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean isTaskFreeform(java.lang.String packageName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isTaskFreeform, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Brightness Control
      @Override public void setSystemBrightness(int brightness) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(brightness);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setSystemBrightness, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public int getSystemBrightness() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getSystemBrightness, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public float getSystemBrightnessFloat() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        float _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getSystemBrightnessFloat, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readFloat();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void setAutoBrightness(boolean enabled) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((enabled)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setAutoBrightness, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean isAutoBrightness() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isAutoBrightness, _data, _reply, 0);
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // Legacy / Direct Hardware Control
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
      // NEW: Alternate Display Off Logic (Targeted)
      @Override public void setBrightness(int displayId, int value) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(displayId);
          _data.writeInt(value);
          boolean _status = mRemote.transact(Stub.TRANSACTION_setBrightness, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
    }
    static final int TRANSACTION_injectKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_forceStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_runCommand = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_setScreenOff = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_repositionTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_getVisiblePackages = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_getVisibleComponents = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_getVisibleTaskComponents = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_getWindowLayouts = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_getAllRunningPackages = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_getTaskId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_moveTaskToBack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_moveTaskToFront = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
    static final int TRANSACTION_setFocusedTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
    static final int TRANSACTION_restoreMinimizedTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
    static final int TRANSACTION_moveTaskToDisplay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
    static final int TRANSACTION_dumpMultiTaskingMethods = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
    static final int TRANSACTION_batchResize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
    static final int TRANSACTION_batchResizeComponents = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
    static final int TRANSACTION_getTaskDebugSnapshot = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
    static final int TRANSACTION_isTaskFreeform = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
    static final int TRANSACTION_setSystemBrightness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
    static final int TRANSACTION_getSystemBrightness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
    static final int TRANSACTION_getSystemBrightnessFloat = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
    static final int TRANSACTION_setAutoBrightness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
    static final int TRANSACTION_isAutoBrightness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
    static final int TRANSACTION_setBrightnessViaDisplayManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
    static final int TRANSACTION_setBrightness = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
  }
  /** @hide */
  public static final java.lang.String DESCRIPTOR = "com.katsuyamaki.DroidOSLauncher.IShellService";
  public void injectKey(int keyCode, int action, int flags, int displayId, int metaState) throws android.os.RemoteException;
  public void forceStop(java.lang.String packageName) throws android.os.RemoteException;
  public void runCommand(java.lang.String command) throws android.os.RemoteException;
  public void setScreenOff(int displayIndex, boolean turnOff) throws android.os.RemoteException;
  public void repositionTask(java.lang.String packageName, java.lang.String className, int left, int top, int right, int bottom) throws android.os.RemoteException;
  public java.util.List<java.lang.String> getVisiblePackages(int displayId) throws android.os.RemoteException;
  public java.util.List<java.lang.String> getVisibleComponents(int displayId) throws android.os.RemoteException;
  public java.util.List<java.lang.String> getVisibleTaskComponents(int displayId) throws android.os.RemoteException;
  public java.util.List<java.lang.String> getWindowLayouts(int displayId) throws android.os.RemoteException;
  public java.util.List<java.lang.String> getAllRunningPackages() throws android.os.RemoteException;
  public int getTaskId(java.lang.String packageName, java.lang.String className) throws android.os.RemoteException;
  public void moveTaskToBack(int taskId) throws android.os.RemoteException;
  public void moveTaskToFront(int taskId, int displayId) throws android.os.RemoteException;
  public void setFocusedTask(int taskId) throws android.os.RemoteException;
  public boolean restoreMinimizedTask(int taskId, int displayId) throws android.os.RemoteException;
  public boolean moveTaskToDisplay(int taskId, int displayId) throws android.os.RemoteException;
  public java.lang.String dumpMultiTaskingMethods() throws android.os.RemoteException;
  public void batchResize(java.util.List<java.lang.String> packages, int[] bounds) throws android.os.RemoteException;
  public void batchResizeComponents(java.util.List<java.lang.String> packages, java.util.List<java.lang.String> classes, int[] bounds) throws android.os.RemoteException;
  public java.lang.String getTaskDebugSnapshot(java.lang.String packageName) throws android.os.RemoteException;
  public boolean isTaskFreeform(java.lang.String packageName) throws android.os.RemoteException;
  // Brightness Control
  public void setSystemBrightness(int brightness) throws android.os.RemoteException;
  public int getSystemBrightness() throws android.os.RemoteException;
  public float getSystemBrightnessFloat() throws android.os.RemoteException;
  public void setAutoBrightness(boolean enabled) throws android.os.RemoteException;
  public boolean isAutoBrightness() throws android.os.RemoteException;
  // Legacy / Direct Hardware Control
  public boolean setBrightnessViaDisplayManager(int displayId, float brightness) throws android.os.RemoteException;
  // NEW: Alternate Display Off Logic (Targeted)
  public void setBrightness(int displayId, int value) throws android.os.RemoteException;
}
