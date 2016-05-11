package cop5555sp15;

import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import static cop5555sp15.TokenStream.Kind.*;

public class Scanner {
	final TokenStream stream;
	int lineNumber = 1;
	int index;

	public Scanner(TokenStream stream) {
		this.stream = stream;
	}

	// Fills in the stream.tokens list with recognized tokens
	// from the input
	public void scan() {
		for (index = 0; index < this.stream.inputChars.length; index++) {
			char currentChar = this.stream.inputChars[index];

			// skip whitespace and handle new line
			if (Character.isWhitespace(currentChar)) {
				if (currentChar == '\n') {
					this.lineNumber++;
				} else if (currentChar == '\r') {
					this.lineNumber++;
					if ((index + 1 < this.stream.inputChars.length)) {
						if (this.stream.inputChars[index + 1] == '\n') {
							index++;
						}
					}
				}
			}

			// single char tokens
			else if (currentChar == '@') {
				Token t = stream.new Token(Kind.AT, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == ';') {
				Token t = stream.new Token(Kind.SEMICOLON, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == '%') {
				Token t = stream.new Token(Kind.MOD, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			// . or ..
			else if (currentChar == '.') {
				boolean isRange = false;
				if ((index + 1 < this.stream.inputChars.length)) {
					if (this.stream.inputChars[index + 1] == '.') {
						Token t = stream.new Token(Kind.RANGE, index,
								index + 2, lineNumber);
						this.stream.tokens.add(t);
						index++;
						isRange = true;
					}
				}
				if (!isRange) {
					Token t = stream.new Token(Kind.DOT, index, index + 1,
							lineNumber);
					this.stream.tokens.add(t);
				}
			}

			else if (currentChar == ',') {
				Token t = stream.new Token(Kind.COMMA, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == '(') {
				Token t = stream.new Token(Kind.LPAREN, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == ')') {
				Token t = stream.new Token(Kind.RPAREN, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == '[') {
				Token t = stream.new Token(Kind.LSQUARE, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == ']') {
				Token t = stream.new Token(Kind.RSQUARE, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == '{') {
				Token t = stream.new Token(Kind.LCURLY, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == '}') {
				Token t = stream.new Token(Kind.RCURLY, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == ':') {
				Token t = stream.new Token(Kind.COLON, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == '?') {
				Token t = stream.new Token(Kind.QUESTION, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == '|') {
				Token t = stream.new Token(Kind.BAR, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == '&') {
				Token t = stream.new Token(Kind.AND, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == '+') {
				Token t = stream.new Token(Kind.PLUS, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			else if (currentChar == '*') {
				Token t = stream.new Token(Kind.TIMES, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			// ! or !=
			else if (currentChar == '!') {
				boolean isNE = false;
				if ((index + 1 < this.stream.inputChars.length)) {
					if (this.stream.inputChars[index + 1] == '=') {
						Token t = stream.new Token(Kind.NOTEQUAL, index,
								index + 2, lineNumber);
						this.stream.tokens.add(t);
						index++;
						isNE = true;
					}
				}
				if (!isNE) {
					Token t = stream.new Token(Kind.NOT, index, index + 1,
							lineNumber);
					this.stream.tokens.add(t);
				}
			}

			// = or ==
			else if (currentChar == '=') {
				boolean isEqual = false;
				if ((index + 1 < this.stream.inputChars.length)) {
					if (this.stream.inputChars[index + 1] == '=') {
						Token t = stream.new Token(Kind.EQUAL, index,
								index + 2, lineNumber);
						this.stream.tokens.add(t);
						index++;
						isEqual = true;
					}
				}
				if (!isEqual) {
					Token t = stream.new Token(Kind.ASSIGN, index, index + 1,
							lineNumber);
					this.stream.tokens.add(t);
				}
			}

			// - or ->
			else if (currentChar == '-') {
				boolean isArrow = false;
				if ((index + 1 < this.stream.inputChars.length)) {
					if (this.stream.inputChars[index + 1] == '>') {
						Token t = stream.new Token(Kind.ARROW, index,
								index + 2, lineNumber);
						this.stream.tokens.add(t);
						index++;
						isArrow = true;
					}
				}
				if (!isArrow) {
					Token t = stream.new Token(Kind.MINUS, index, index + 1,
							lineNumber);
					this.stream.tokens.add(t);
				}
			}

			// < or <= or <<
			else if (currentChar == '<') {
				boolean isOther = false;
				if ((index + 1 < this.stream.inputChars.length)) {
					if (this.stream.inputChars[index + 1] == '=') {
						Token t = stream.new Token(Kind.LE, index, index + 2,
								lineNumber);
						this.stream.tokens.add(t);
						index++;
						isOther = true;
					}

					else if (this.stream.inputChars[index + 1] == '<') {
						Token t = stream.new Token(Kind.LSHIFT, index,
								index + 2, lineNumber);
						this.stream.tokens.add(t);
						index++;
						isOther = true;
					}
				}
				if (!isOther) {
					Token t = stream.new Token(Kind.LT, index, index + 1,
							lineNumber);
					this.stream.tokens.add(t);
				}
			}

			// > or >= or >>
			else if (currentChar == '>') {
				boolean isOther = false;
				if ((index + 1 < this.stream.inputChars.length)) {
					if (this.stream.inputChars[index + 1] == '=') {
						Token t = stream.new Token(Kind.GE, index, index + 2,
								lineNumber);
						this.stream.tokens.add(t);
						index++;
						isOther = true;
					}

					else if (this.stream.inputChars[index + 1] == '>') {
						Token t = stream.new Token(Kind.RSHIFT, index,
								index + 2, lineNumber);
						this.stream.tokens.add(t);
						index++;
						isOther = true;
					}
				}
				if (!isOther) {
					Token t = stream.new Token(Kind.GT, index, index + 1,
							lineNumber);
					this.stream.tokens.add(t);
				}
			}

			// handles / or comments
			else if (currentChar == '/') {
				int currentIndex = index;
				boolean goodComment = false;
				boolean isComment = false;
				if ((index + 1 < this.stream.inputChars.length)) {
					if (this.stream.inputChars[index + 1] == '*') {
						boolean isDone = false;
						for (int i = index + 2; i < this.stream.inputChars.length
								&& !isDone; i++) {
							if (this.stream.inputChars[i] == '*') {
								if ((i + 1 < this.stream.inputChars.length)) {
									if (this.stream.inputChars[i + 1] == '/') {
										goodComment = true;
										index = i + 1; // used to be index = i +
														// 2
										isDone = true;
									}
								}
							}
						}
						if (!goodComment) {
							Token t = stream.new Token(
									Kind.UNTERMINATED_COMMENT, currentIndex,
									this.stream.inputChars.length, lineNumber);
							this.stream.tokens.add(t);
							index = this.stream.inputChars.length - 1;
						}
						isComment = true;
					}
				}
				if (!isComment) {
					Token t = stream.new Token(Kind.DIV, index, index + 1,
							lineNumber);
					this.stream.tokens.add(t);
				}
			}

			else if (currentChar == '0') {
				Token t = stream.new Token(Kind.INT_LIT, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);
			}

			// numlits
			else if (this.isDigit(currentChar)) {
				int currentIndex = index;
				boolean isDone = false;
				int i;
				for (i = index + 1; i < this.stream.inputChars.length
						&& !isDone; i++) {
					if (this.isDigit(this.stream.inputChars[i])
							|| this.stream.inputChars[i] == '0') {
						// continue
					} else {
						isDone = true;
						index = i - 1;
					}
				}
				if (isDone == false) {
					index = i - 1;
				}
				Token t = stream.new Token(Kind.INT_LIT, currentIndex,
						index + 1, lineNumber);
				this.stream.tokens.add(t);
			}

			// idents and keywords
			else if (Character.isJavaIdentifierStart(currentChar)) {
				int currentIndex = index;
				boolean isDone = false;
				int i;
				for (i = index + 1; i < this.stream.inputChars.length
						&& !isDone; i++) {
					if (Character
							.isJavaIdentifierPart(this.stream.inputChars[i])) {
						// continue
					} else {
						isDone = true;
						index = i - 1;
					}
				}
				if (isDone == false) {
					index = i - 1;
				}
				// System.out.println("length " +
				// this.stream.inputChars.length);
				// System.out.println(currentIndex + " " + index);
				String s = new String(this.stream.inputChars, currentIndex,
						index - currentIndex + 1);
				// System.out.println(s);
				if (s.equals("int")) {
					Token t = stream.new Token(Kind.KW_INT, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("string")) {
					Token t = stream.new Token(Kind.KW_STRING, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("boolean")) {
					Token t = stream.new Token(Kind.KW_BOOLEAN, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("import")) {
					Token t = stream.new Token(Kind.KW_IMPORT, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("class")) {
					Token t = stream.new Token(Kind.KW_CLASS, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("def")) {
					Token t = stream.new Token(Kind.KW_DEF, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("while")) {
					Token t = stream.new Token(Kind.KW_WHILE, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("if")) {
					Token t = stream.new Token(Kind.KW_IF, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("else")) {
					Token t = stream.new Token(Kind.KW_ELSE, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("return")) {
					Token t = stream.new Token(Kind.KW_RETURN, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("print")) {
					Token t = stream.new Token(Kind.KW_PRINT, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("true")) {
					Token t = stream.new Token(Kind.BL_TRUE, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("false")) {
					Token t = stream.new Token(Kind.BL_FALSE, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("null")) {
					Token t = stream.new Token(Kind.NL_NULL, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("size")) {
					Token t = stream.new Token(Kind.KW_SIZE, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("key")) {
					Token t = stream.new Token(Kind.KW_KEY, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else if (s.equals("value")) {
					Token t = stream.new Token(Kind.KW_VALUE, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else {
					Token t = stream.new Token(Kind.IDENT, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				}

			}

			// handles strings
			else if (currentChar == '"') {
				int currentIndex = index;
				boolean isDone = false;
				int i;
				for (i = index + 1; i < this.stream.inputChars.length
						&& !isDone; i++) {
					if ((this.stream.inputChars[i] != '"')
							|| ((this.stream.inputChars[i] == '"') && (this.stream.inputChars[i - 1] == '\\'))) {
						// continue
					} else {
						isDone = true;
						index = i;
					}
				}
				if (isDone) {
					Token t = stream.new Token(Kind.STRING_LIT, currentIndex,
							index + 1, lineNumber);
					this.stream.tokens.add(t);
				} else {
					index = i - 1;
					Token t = stream.new Token(Kind.UNTERMINATED_STRING,
							currentIndex, index + 1, lineNumber);
					this.stream.tokens.add(t);
				}
			}

			// final case which handles illegal characters
			else {
				Token t = stream.new Token(Kind.ILLEGAL_CHAR, index, index + 1,
						lineNumber);
				this.stream.tokens.add(t);

			}
		}

		// always make EOF token
		Token t = stream.new Token(Kind.EOF, index, index, lineNumber);
		this.stream.tokens.add(t);

	}

	// checks if input character is 1..9
	public boolean isDigit(char c) {
		if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5'
				|| c == '6' || c == '7' || c == '8' || c == '9') {
			return true;
		} else {
			return false;
		}
	}

	// reset state
	public void reset() {
		this.index = 0;
		this.lineNumber = 1;

	}

}
