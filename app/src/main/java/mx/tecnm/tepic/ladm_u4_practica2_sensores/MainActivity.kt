package mx.tecnm.tepic.ladm_u4_practica2_sensores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sm : SensorManager
    lateinit var imagen: ImageView
    lateinit var acelerometro: Sensor
    lateinit var magnetometro: Sensor
    var gradosAct = 0.0f
    var aceleroFin = FloatArray(3)
    var magnetoFin = FloatArray(3)
    var aceleroFinSet = false
    var magnetoFinSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        imagen = findViewById(R.id.imageViewCompass) as ImageView
        acelerometro = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometro = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        AlertDialog.Builder(this).setTitle("Aviso").setMessage("Los sensores que se utilizaron en esta aplicacion son el acelerometro y el magnetometro, para apreciarlo de manera correcta se recomienta utilizar un dispositivo android fisico uwu").show()

    }

    override fun onSensorChanged(p0: SensorEvent) {
        if (p0.sensor === acelerometro) {
            transformar(p0.values, aceleroFin)
            aceleroFinSet = true
        } else if (p0.sensor === magnetometro) {
            transformar(p0.values, magnetoFin)
            magnetoFinSet = true
        }

        if (aceleroFinSet && magnetoFinSet) {
            val r = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, null, aceleroFin, magnetoFin)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                val grados = (Math.toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360



                val rotateAnimation = RotateAnimation(gradosAct,-grados,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)
                rotateAnimation.duration = 1000
                rotateAnimation.fillAfter = true
                imagen.startAnimation(rotateAnimation)
                gradosAct = -grados
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onResume() {
        super.onResume()
        sm.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME)
        sm.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sm.unregisterListener(this, acelerometro)
        sm.unregisterListener(this, magnetometro)
    }

    fun transformar(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f
        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }

}