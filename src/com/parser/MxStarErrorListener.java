package com.parser;

import com.utility.ErrorHandler;
import com.utility.Location;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class MxStarErrorListener extends BaseErrorListener {
    private ErrorHandler errorHandler;

    public MxStarErrorListener(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        errorHandler.error(new Location(line, charPositionInLine), msg);
    }

}
