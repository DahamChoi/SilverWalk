package com.gsc.silverwalk

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_finish_mission.*

class FinishMissionActivity : AppCompatActivity() {

    var averagePace = 0L
    var calories = 0L
    var distance = 0L
    var heartRate = 0L
    var location = ""
    var steps = 0L
    var time = 0L
    var level = 0L

    // UID
    private val UID = "X5NztOqVUgGPT84WmSiK"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_mission)

        // Get Intent Data
        averagePace =
                if (intent.hasExtra("averagePace"))
                    (intent.getLongExtra("averagePace",0)) else averagePace
        calories =
                if (intent.hasExtra("calories"))
                    (intent.getLongExtra("calories",0)) else calories
        distance =
                if (intent.hasExtra("distance"))
                    (intent.getLongExtra("distance",0)) else distance
        heartRate =
                if (intent.hasExtra("heartRate"))
                    (intent.getLongExtra("heartRate",0)) else heartRate
        location =
                if (intent.hasExtra("location"))
                    (intent.getStringExtra("location")!!) else location
        steps =
                if (intent.hasExtra("steps"))
                    (intent.getLongExtra("steps",0)) else steps
        time =
                if (intent.hasExtra("time"))
                    (intent.getLongExtra("time",0)) else time

        supportActionBar?.hide()

        activity_finish_mission_easy_cardview.setOnClickListener(View.OnClickListener {
            selectLevel(0)
        })

        activity_finish_mission_moderate_cardview.setOnClickListener(View.OnClickListener {
            selectLevel(1)
        })

        activity_finish_mission_hard_cardview.setOnClickListener(View.OnClickListener {
            selectLevel(2)
        })

        activity_finish_share_button.setOnClickListener(View.OnClickListener {
            shareMission()
        })

        activity_finish_end_button.setOnClickListener(View.OnClickListener {
            endMission()
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun selectLevel(level: Long){
        this.level = level

        if(this.level == 0L){
            activity_finish_mission_easy_cardview
                    .setCardBackgroundColor(getColor(R.color.google_key_color))
            activity_finish_mission_moderate_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_hard_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_easy_text.setTextColor(
                    getColor(R.color.white))
            activity_finish_mission_moderate_text.setTextColor(
                    getColor(R.color.black))
            activity_finish_mission_hard_text.setTextColor(
                    getColor(R.color.black))
        }else if(this.level == 1L){
            activity_finish_mission_easy_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_moderate_cardview
                    .setCardBackgroundColor(getColor(R.color.google_key_color))
            activity_finish_mission_hard_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_easy_text.setTextColor(getColor(R.color.black))
            activity_finish_mission_moderate_text.setTextColor(getColor(R.color.white))
            activity_finish_mission_hard_text.setTextColor(getColor(R.color.black))
        }else if(this.level == 2L){
            activity_finish_mission_easy_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_moderate_cardview
                    .setCardBackgroundColor(getColor(R.color.white))
            activity_finish_mission_hard_cardview
                    .setCardBackgroundColor(getColor(R.color.google_key_color))
            activity_finish_mission_easy_text.setTextColor(getColor(R.color.black))
            activity_finish_mission_moderate_text.setTextColor(getColor(R.color.black))
            activity_finish_mission_hard_text.setTextColor(getColor(R.color.white))
        }

        activity_finish_mission_button_linearlayout.visibility = View.VISIBLE
    }

    fun shareMission(){
        val ShareActivityIntent = Intent(this, ShareActivity::class.java)
        startActivity(ShareActivityIntent)
    }

    fun endMission(){
        // Progress Dialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Wait a second...")
        progressDialog.show()

        // History Data
        val data = hashMapOf(
                "average_pace" to averagePace,
                "calories" to calories,
                "distance" to distance,
                "heart_rate" to heartRate,
                "location" to location,
                "steps" to steps,
                "time" to Timestamp.now(),
                "time_second" to time
        )

        // Firebase History Collection Add
        Firebase.firestore
                .collection("users")
                .document(UID)
                .collection("history")
                .add(data)
                .addOnSuccessListener {
                    progressDialog.cancel()
                    finish()
                }
                .addOnFailureListener {

                }
    }
}