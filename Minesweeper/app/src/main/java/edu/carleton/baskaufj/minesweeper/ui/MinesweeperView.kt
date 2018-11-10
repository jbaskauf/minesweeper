package edu.carleton.baskaufj.minesweeper.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import edu.carleton.baskaufj.minesweeper.MainActivity
import edu.carleton.baskaufj.minesweeper.R
import edu.carleton.baskaufj.minesweeper.model.MinesweeperModel
import edu.carleton.baskaufj.minesweeper.model.MinesweeperModel.COVERED
import edu.carleton.baskaufj.minesweeper.model.MinesweeperModel.FLAG
import edu.carleton.baskaufj.minesweeper.model.MinesweeperModel.FLAGGED
import edu.carleton.baskaufj.minesweeper.model.MinesweeperModel.UNCOVERED

class MinesweeperView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paintCoveredBoard = Paint()
    private val paintLine = Paint()
    private val paintTextWhite = Paint()
    private val paintUncoveredBoard = Paint()
    private val paintTextOrange = Paint()

    init {
        //light purple covered game area
        paintCoveredBoard.color = Color.parseColor("#5C6BC0")

        //dark purple uncovered game area
        paintUncoveredBoard.color = Color.parseColor("#303F9F")

        //white lines defining game area
        paintLine.color = Color.WHITE
        paintLine.strokeWidth = 10F
        paintLine.style = Paint.Style.STROKE

        //white text
        paintTextWhite.color = Color.WHITE
        paintTextWhite.textSize = 100F

        //orange text
        paintTextOrange.color = Color.parseColor("#F57C00")
        paintTextOrange.textSize = 100F
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        paintTextWhite.textSize = height.toFloat() / MinesweeperModel.numRows
        paintTextOrange.textSize = height.toFloat() / MinesweeperModel.numRows

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintCoveredBoard)
        drawGameArea(canvas)
        drawAllSymbols(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintLine)
    }

    fun drawGameArea(canvas: Canvas) {
        //draw horizontal lines
        for (i in 1..MinesweeperModel.numRows-1) {
            canvas.drawLine(0f, (i * height / MinesweeperModel.numRows).toFloat(), width.toFloat(), (i * height / MinesweeperModel.numRows).toFloat(),
                    paintLine)
        }
        //draw vertical lines
        for (i in 1..MinesweeperModel.numColumns-1) {
            canvas.drawLine((i * width / MinesweeperModel.numColumns).toFloat(), 0f, (i * width / MinesweeperModel.numColumns).toFloat(), height.toFloat(),
                    paintLine)
        }
    }

    //draw numbers and symbols in the fields of the MinesweeperView
    fun drawAllSymbols(canvas: Canvas) {
        for (i in 0 until MinesweeperModel.numColumns) {
            for (j in 0 until MinesweeperModel.numRows) {
                drawSymbol(i, j, canvas)
            }
        }
    }

    //draw a single symbol in a given field of the MinesweeperView based on its content in the model
    private fun drawSymbol(i: Int, j: Int, canvas: Canvas) {
        val squareWidth = width.toFloat() / MinesweeperModel.numColumns
        val squareHeight = height.toFloat() / MinesweeperModel.numRows
        //if FLAGGED, draw little flag symbol
        if (MinesweeperModel.getCoverState(i, j) == FLAGGED) {
            //add flag symbol
            canvas.drawText("⚐", i * squareWidth + (.13 * squareWidth).toFloat(), (j + 1) * squareHeight - (.15 * squareHeight).toFloat(), paintTextOrange)
        }
        //if UNCOVERED, draw mine or mine counter number
        if (MinesweeperModel.getCoverState(i, j) == UNCOVERED) {
            //gray out the background
            canvas.drawRect(i * squareWidth, j * squareHeight, (i + 1) * squareWidth, (j + 1) * squareHeight, paintUncoveredBoard)
            if (MinesweeperModel.isMine(i, j)) {
                //add mine symbol
                canvas.drawText("⏣", i * squareWidth + (.12 * squareWidth).toFloat(), (j + 1) * squareHeight - (.13 * squareHeight).toFloat(), paintTextOrange)
            } else {
                var counter = MinesweeperModel.getMineCounter(i, j)
                if (counter != 0.toShort()) {
                    //add number of mines nearby
                    canvas.drawText(counter.toString(), i * squareWidth + (.22 * squareWidth).toFloat(), (j + 1) * squareHeight - (.14 * squareHeight).toFloat(), paintTextWhite)
                }
            }
        }
    }

    //update the view and model based on the conditions of the game at the time and location of the touch event
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val tX = event.x.toInt() / (width / MinesweeperModel.numColumns)
        val tY = event.y.toInt() / (height / MinesweeperModel.numRows)

        //if the game is not over, change the state of the field at (tX, tY) in the model
        if ((MinesweeperModel.gameOver == false) && (MinesweeperModel.isWin == false) && (tX < MinesweeperModel.numColumns) && (tY < MinesweeperModel.numRows)) {
            //if no flags are left, display snackbar message
            if (MinesweeperModel.mode == FLAG && MinesweeperModel.getCoverState(tX, tY) == COVERED && MinesweeperModel.numFlags == 0) (context as MainActivity).displaySnackbar(context.getString(R.string.no_flags))
            MinesweeperModel.changeSquareState(tX, tY)
            (context as MainActivity).showFlagNumber(MinesweeperModel.numFlags)
        }
        if (MinesweeperModel.gameOver) (context as MainActivity).displaySnackbar(context.getString(R.string.game_over))
        if (MinesweeperModel.isWin) (context as MainActivity).displaySnackbar(context.getString(R.string.you_win))

        invalidate()

        return super.onTouchEvent(event)
    }


    fun restart() {
        MinesweeperModel.resetModel()
        (context as MainActivity).showFlagNumber(MinesweeperModel.numFlags)
        invalidate()
    }
}