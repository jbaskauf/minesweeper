package edu.carleton.baskaufj.minesweeper

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import edu.carleton.baskaufj.minesweeper.model.MinesweeperModel
import edu.carleton.baskaufj.minesweeper.model.MinesweeperModel.FLAG
import edu.carleton.baskaufj.minesweeper.model.MinesweeperModel.TRY
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize flagText view text
        showFlagNumber(MinesweeperModel.numFlags)

        btnRestart.setOnClickListener {
            minesweeperView.restart()
            toggleFlag.isChecked = false
        }

        toggleFlag.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                MinesweeperModel.mode = FLAG
            } else {
                MinesweeperModel.mode = TRY
            }
        }
    }

    fun showFlagNumber(numFlags: Int) {
        //flag text view = numFlags
        flagText.text = getString(R.string.num_flags, numFlags.toString())
    }

    fun displaySnackbar(msg: String) {
        //make a snackbar saying "you win" or "game over"
        Snackbar.make(minesweeperView, msg, Snackbar.LENGTH_LONG).show()
    }
}
