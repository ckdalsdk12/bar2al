package com.ckdalsdk12.bar2al

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ckdalsdk12.bar2al.databinding.ActivityBarcodeScannerBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val CAMERA_PERMISSION_REQUEST_CODE = 1 // 권한 요청에 사용할 요청 코드

@ExperimentalGetImage
class BarcodeScanner : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeScannerBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 액티비티 실행시 카메라 권한 확인
        // 권한이 허용 되어 있을 경우 bindCameraUseCases 메소드 실행
        if (hasCameraPermission()) bindCameraUseCases()
        else requestPermission() // 허용 되어 있지 않을 경우 퍼미션 요청
    }
    
    // 카메라 권한이 허용 되어 있을 경우 true 반환
    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    // 카메라 권한 요청 창을 띄움. CAMERA_PERMISSION_REQUEST_CODE = 1
    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    // 사용자가 권한 요청에 응답하면 onRequestPermissionsResult 메소드 실행
    // 카메라 권한을 허용하면 bindCameraUseCases 메소드 실행
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            bindCameraUseCases()
        } else {
            Toast.makeText(this,
                "이 앱에는 카메라 권한이 필요합니다.",
                Toast.LENGTH_LONG
            ).show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // CameraX와 ML Kit을 사용하여 카메라 프리뷰 창과 바코드 분석기를 실행
    private fun bindCameraUseCases() {
        // ProcessCameraProvider 객체를 반환시켜
        // 카메라의 생명주기를 액티비티와 같은 LifeCycleOwner의 생명주기에 바인딩
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // cameraProvider에 ProcessCameraProvider 객체 가져오기
            val cameraProvider = cameraProviderFuture.get()

            // 카메라 프리뷰를 만듬
            val previewUseCase = Preview.Builder()
                .build()
                .also {
                    // 카메라 프리뷰를 레이아웃의 PreviewView에 연결
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            // 바코드 스캐너를 설정. 13자리 바코드만 인식하도록 설정
            val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_EAN_13
            ).build()

            // 위 옵션을 주어 바코드 스캐너 객체를 생성
            val scanner = BarcodeScanning.getClient(options)

            // ImageAnalysis(이미지 분석기)를 만듬
            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // 이미지 분석기의 기능을 정의. processImageProxy 메소드 실행
            cameraExecutor = Executors.newSingleThreadExecutor()
            analysisUseCase.setAnalyzer(
                cameraExecutor
            ) { imageProxy ->
                processImageProxy(scanner, imageProxy)
            }

            // cameraSelector를 사용하여 뒷면 카메라를 사용하도록 지정
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 카메라를 수명주기에 결합
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    previewUseCase,
                    analysisUseCase)
            } catch (illegalStateException: IllegalStateException) {
                // If the use case has already been bound to another lifecycle or method is not called on main thread.
                Log.e(TAG, illegalStateException.message.orEmpty())
            } catch (illegalArgumentException: IllegalArgumentException) {
                // If the provided camera selector is unable to resolve a camera to be used for the given use cases.
                Log.e(TAG, illegalArgumentException.message.orEmpty())
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        // 이미지와 회전 값 가져오기
        imageProxy.image?.let { image ->
            val inputImage =
                InputImage.fromMediaImage(
                    image,
                    imageProxy.imageInfo.rotationDegrees
                )

            // 바코드 스캐너 실행
            barcodeScanner.process(inputImage)
                // 성공적으로 바코드를 스캔하면 barcodeValue에 값을 저장하고 bottomText 텍스트 뷰에 대입
                .addOnSuccessListener { barcodeList ->
                    val barcode = barcodeList.getOrNull(0)
                    
                    barcode?.rawValue?.let { value ->
                        binding.bottomText.text =
                            getString(R.string.barcode_value, value)
                        val intent = Intent(this, ResultAL::class.java)
                        intent.putExtra("barcodeValue", barcode.rawValue)
                        startActivity(intent)
                        cameraProviderFuture.cancel(true)
                        cameraExecutor.shutdownNow()
                        finish()
                    }
                }
                .addOnFailureListener {
                    // 바코드 스캐너 실패시
                    Log.e(TAG, it.message.orEmpty())
                }.addOnCompleteListener {
                    // CameraX 분석 사용 시 종료 후 수신된 이미지에 대해 image.close()를 호출해야 함
                    imageProxy.image?.close()
                    imageProxy.close()
                }
        }
    }

    companion object {
        val TAG: String = BarcodeScanner::class.java.simpleName
    }
}