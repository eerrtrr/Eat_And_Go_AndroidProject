package fr.isen.meloni.eatandgo.ble

import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.meloni.eatandgo.databinding.ActivityBleDetailsBinding


class BLEDetailsActivity : AppCompatActivity() {

    //binding
    private lateinit var binding : ActivityBleDetailsBinding
    var bluetoothGatt: BluetoothGatt? = null
    var statut: String = "statut :  "

    //init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init binding instance
        binding = ActivityBleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get device info
        val device = intent.getParcelableExtra<BluetoothDevice>("BLEDeviceInfo")

        //display device info
        binding.deviceDetailBle.text = device?.name ?: "Appareil inconnue"
        bluetoothGatt = device?.connectGatt(this, true, gattCallback)


        bluetoothGatt?.connect()
        connectToDevice(device)

    }
    private fun connectToDevice (device: BluetoothDevice?) {
        bluetoothGatt = device?.connectGatt(this, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    runOnUiThread {
                        statut = STATE_CONNECTED
                        binding.deviceStatut.text = "status : " + statut
                    }
                    bluetoothGatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    runOnUiThread {
                        statut = STATE_DISCONNECTED
                        binding.deviceStatut.text = "status : " + statut
                    }
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            runOnUiThread {
                binding.recBle.adapter?.notifyDataSetChanged()
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            runOnUiThread {
                binding.recBle.adapter?.notifyDataSetChanged()
            }
        }
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            runOnUiThread {
                binding.recBle.adapter?.notifyDataSetChanged()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            runOnUiThread {
                binding.recBle.adapter = DetailBleAdapter(
                    gatt,
                    gatt?.services?.map {
                        BLEService(
                            it.uuid.toString(),
                            it.characteristics
                        )
                    }?.toMutableList() ?: arrayListOf(), this@BLEDetailsActivity)
                binding.recBle.layoutManager = LinearLayoutManager(this@BLEDetailsActivity)
            }
        }
    }

    companion object {
        private const val STATE_DISCONNECTED = "Déconnecté"
        private const val STATE_CONNECTED = "Connecté"
    }
}

