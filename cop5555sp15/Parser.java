package cop5555sp15;

import static cop5555sp15.TokenStream.Kind.AND;
import static cop5555sp15.TokenStream.Kind.ARROW;
import static cop5555sp15.TokenStream.Kind.ASSIGN;
import static cop5555sp15.TokenStream.Kind.AT;
import static cop5555sp15.TokenStream.Kind.BAR;
import static cop5555sp15.TokenStream.Kind.BL_FALSE;
import static cop5555sp15.TokenStream.Kind.BL_TRUE;
import static cop5555sp15.TokenStream.Kind.COLON;
import static cop5555sp15.TokenStream.Kind.COMMA;
import static cop5555sp15.TokenStream.Kind.DIV;
import static cop5555sp15.TokenStream.Kind.DOT;
import static cop5555sp15.TokenStream.Kind.EOF;
import static cop5555sp15.TokenStream.Kind.EQUAL;
import static cop5555sp15.TokenStream.Kind.GE;
import static cop5555sp15.TokenStream.Kind.GT;
import static cop5555sp15.TokenStream.Kind.IDENT;
import static cop5555sp15.TokenStream.Kind.INT_LIT;
import static cop5555sp15.TokenStream.Kind.KW_BOOLEAN;
import static cop5555sp15.TokenStream.Kind.KW_CLASS;
import static cop5555sp15.TokenStream.Kind.KW_DEF;
import static cop5555sp15.TokenStream.Kind.KW_ELSE;
import static cop5555sp15.TokenStream.Kind.KW_IF;
import static cop5555sp15.TokenStream.Kind.KW_IMPORT;
import static cop5555sp15.TokenStream.Kind.KW_INT;
import static cop5555sp15.TokenStream.Kind.KW_PRINT;
import static cop5555sp15.TokenStream.Kind.KW_RETURN;
import static cop5555sp15.TokenStream.Kind.KW_STRING;
import static cop5555sp15.TokenStream.Kind.KW_WHILE;
import static cop5555sp15.TokenStream.Kind.LCURLY;
import static cop5555sp15.TokenStream.Kind.LE;
import static cop5555sp15.TokenStream.Kind.LPAREN;
import static cop5555sp15.TokenStream.Kind.LSHIFT;
import static cop5555sp15.TokenStream.Kind.LSQUARE;
import static cop5555sp15.TokenStream.Kind.LT;
import static cop5555sp15.TokenStream.Kind.MINUS;
import static cop5555sp15.TokenStream.Kind.MOD;
import static cop5555sp15.TokenStream.Kind.NOT;
import static cop5555sp15.TokenStream.Kind.NOTEQUAL;
import static cop5555sp15.TokenStream.Kind.PLUS;
import static cop5555sp15.TokenStream.Kind.RANGE;
import static cop5555sp15.TokenStream.Kind.RCURLY;
import static cop5555sp15.TokenStream.Kind.RPAREN;
import static cop5555sp15.TokenStream.Kind.RSHIFT;
import static cop5555sp15.TokenStream.Kind.RSQUARE;
import static cop5555sp15.TokenStream.Kind.SEMICOLON;
import static cop5555sp15.TokenStream.Kind.STRING_LIT;
import static cop5555sp15.TokenStream.Kind.TIMES;
import static cop5555sp15.TokenStream.Kind.KW_SIZE;
import static cop5555sp15.TokenStream.Kind.KW_KEY;
import static cop5555sp15.TokenStream.Kind.KW_VALUE;
import cop5555sp15.Parser.SyntaxException;
import cop5555sp15.TokenStream.Kind;
import cop5555sp15.TokenStream.Token;
import cop5555sp15.ast.*;

import java.util.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;
		Kind[] expected;
		String msg;

		SyntaxException(Token t, Kind expected) {
			this.t = t;
			msg = "";
			this.expected = new Kind[1];
			this.expected[0] = expected;

		}

		public SyntaxException(Token t, String msg) {
			this.t = t;
			this.msg = msg;
		}

		public SyntaxException(Token t, Kind[] expected) {
			this.t = t;
			msg = "";
			this.expected = expected;
		}

		public String getMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append(" error at token ").append(t.toString()).append(" ")
					.append(msg);
			sb.append(". Expected: ");
			for (Kind kind : expected) {
				sb.append(kind).append(" ");
			}
			return sb.toString();
		}
	}

	TokenStream tokens;
	Token t;

	Parser(TokenStream tokens) {
		this.tokens = tokens;
		t = tokens.nextToken();
	}

	private Kind match(Kind kind) throws SyntaxException {
		if (isKind(kind)) {
			consume();
			return kind;
		}
		throw new SyntaxException(t, kind);
	}

	private Kind match(Kind... kinds) throws SyntaxException {
		Kind kind = t.kind;
		if (isKind(kinds)) {
			consume();
			return kind;
		}
		StringBuilder sb = new StringBuilder();
		for (Kind kind1 : kinds) {
			sb.append(kind1).append(kind1).append(" ");
		}
		throw new SyntaxException(t, "expected one of " + sb.toString());
	}

	private boolean isKind(Kind kind) {
		return (t.kind == kind);
	}

	private void consume() {
		if (t.kind != EOF)
			t = tokens.nextToken();
	}

	private boolean isKind(Kind... kinds) {
		for (Kind kind : kinds) {
			if (t.kind == kind)
				return true;
		}
		return false;
	}

	// This is a convenient way to represent fixed sets of
	// token kinds. You can pass these to isKind.
	static final Kind[] REL_OPS = { BAR, AND, EQUAL, NOTEQUAL, LT, GT, LE, GE };
	static final Kind[] WEAK_OPS = { PLUS, MINUS };
	static final Kind[] STRONG_OPS = { TIMES, DIV };
	static final Kind[] VERY_STRONG_OPS = { LSHIFT, RSHIFT };

	List<SyntaxException> exceptionList = new ArrayList<SyntaxException>();

	public Program parse() { // returns program
		Program p = null;

		try {
			p = Program();
			if (p != null) {
				match(EOF);
			}

		} catch (SyntaxException e) {
			exceptionList.add(e);
		}

		if (exceptionList.isEmpty()) {
			return p;
		} else {
			return null;
		}

	}

	private Program Program() {
		Token firstToken = this.t;
		List<QualifiedName> imports = null;
		Block block = null;
		String name = "";
		try {
			imports = ImportListHelper();
			match(KW_CLASS);
			name = t.getText();
			match(IDENT);
			// List<BlockElem> elems = new ArrayList<BlockElem>(); //debug line
			// remember to remove
			// block = new Block(t, elems); //debug line remember to remove
			block = Block();
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {
			Program p = new Program(firstToken, imports, name, block);
			return p;
		} else {
			return null;
		}
	}

	private List<QualifiedName> ImportListHelper() {
		List<QualifiedName> imports = new ArrayList<QualifiedName>();
		if (!this.isKind(KW_IMPORT)) {
			return imports; // return empty imports
		}
		while (this.isKind(KW_IMPORT)) {
			QualifiedName q = ImportList();
			imports.add(q);
		}
		return imports;
	}

	private QualifiedName ImportList() {
		QualifiedName q = null;
		String s = "";
		Token firstToken = this.t;
		try {
			match(KW_IMPORT);
			s = t.getText();
			match(IDENT);

			while (this.isKind(DOT)) {
				s = s + "/";
				match(DOT);
				s = s + t.getText();
				match(IDENT);
			}
			match(SEMICOLON);
			q = new QualifiedName(firstToken, s);

		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {
			return q;
		} else {
			return null;
		}

	}

	private Block Block() {
		Token firstToken = this.t;
		Block b = null;
		List<BlockElem> elems = new ArrayList<BlockElem>();
		try {
			match(LCURLY);
			while (!this.isKind(RCURLY)) {
				BlockElem be = null;
				if (this.isKind(KW_DEF)) {
					be = Declaration();
					match(SEMICOLON);
				} else {
					be = Statement();
					match(SEMICOLON);
				}
				if (be != null) {
					elems.add(be);
				}
			}
			match(RCURLY);
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {
			b = new Block(firstToken, elems);
			return b;
		} else {
			return null;
		}

	}

	private Statement Statement() {
		Statement statement = null;
		Token firstToken = this.t;
		try {
			if (this.isKind(SEMICOLON)) {
				return null;
			} else if (this.isKind(IDENT)) {
				LValue lv = null;
				Expression ex = null;
				lv = Lvalue();
				match(ASSIGN);
				ex = Expression();
				statement = new AssignmentStatement(firstToken, lv, ex);
			} else if (this.isKind(KW_PRINT)) {
				Expression ex = null;
				match(KW_PRINT);
				ex = Expression();
				statement = new PrintStatement(firstToken, ex);
			} else if (this.isKind(KW_WHILE)) {
				boolean star = false; // flag for star while
				boolean range = false; // flag for range while
				Block b = null;
				Expression ex1 = null;
				Expression ex2 = null;
				RangeExpression re = null;
				match(KW_WHILE);
				if (this.isKind(TIMES)) {
					star = true;
					match(TIMES);
				}
				match(LPAREN);
				Token firstTokenR = this.t; // first token in expression
				ex1 = Expression();
				if (this.isKind(RANGE)) {
					range = true;
					match(RANGE);
					ex2 = Expression();
				}
				match(RPAREN);
				b = Block();

				if (star == true && range == true) { // range while
					re = new RangeExpression(firstTokenR, ex1, ex2);
					statement = new WhileRangeStatement(firstToken, re, b);
				} else if (star == true && range == false) { // star while
					statement = new WhileStarStatement(firstToken, ex1, b);
				} else if (star == false && range == false) {// regular while
					statement = new WhileStatement(firstToken, ex1, b);
				} else {
					// we have a problem
					// throw new SyntaxException(firstTokenR, "invalid while");
				}
			} else if (this.isKind(KW_IF)) {
				boolean isElse = false;
				Block b1 = null;
				Block b2 = null;
				Expression ex = null;

				match(KW_IF);
				match(LPAREN);
				ex = Expression();
				match(RPAREN);
				b1 = Block();
				if (this.isKind(KW_ELSE)) {
					isElse = true;
					match(KW_ELSE);
					b2 = Block();
				}

				if (isElse) {
					statement = new IfElseStatement(firstToken, ex, b1, b2);
				} else {
					statement = new IfStatement(firstToken, ex, b1);
				}
			} else if (this.isKind(MOD)) {
				Expression ex = null;
				match(MOD);
				ex = Expression();
				statement = new ExpressionStatement(firstToken, ex);

			} else if (this.isKind(KW_RETURN)) {
				Expression ex = null;
				match(KW_RETURN);
				ex = Expression();
				statement = new ReturnStatement(firstToken, ex);
			}

			else { // this else should be removed maybe
				match(LPAREN);
				Expression();
				if (this.isKind(RANGE)) {
					match(RANGE);
					Expression();
				}
				match(RPAREN);
				Block();

			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {

			return statement;
		} else {
			return null;
		}

	}

	private Expression Expression() {
		boolean isRelop = false;
		Token firstToken = this.t;
		// Expression toReturn = null;
		Expression ex1 = null;
		Expression ex2 = null;
		BinaryExpression tmp = null;
		try {
			ex1 = Term();
			while (this.isKind(REL_OPS)) {

				Token relToken = this.t;
				match(REL_OPS);
				ex2 = Term();
				if (!isRelop) {
					tmp = new BinaryExpression(firstToken, ex1, relToken, ex2);
				} else {
					BinaryExpression be = null;
					be = new BinaryExpression(firstToken, tmp, relToken, ex2);
					tmp = be;
				}

				isRelop = true;
			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {
			if (isRelop) {
				return tmp;
			} else {
				return ex1;
			}

		} else {
			return null;
		}
	}

	private Expression Term() {
		boolean isWeakop = false;
		Token firstToken = this.t;
		// Expression toReturn = null;
		Expression ex1 = null;
		Expression ex2 = null;
		BinaryExpression tmp = null;

		try {
			ex1 = Elem();
			while (this.isKind(WEAK_OPS)) {
				Token weakToken = this.t;
				match(WEAK_OPS);
				ex2 = Elem();
				if (!isWeakop) {
					tmp = new BinaryExpression(firstToken, ex1, weakToken, ex2);
				} else {
					BinaryExpression be = null;
					be = new BinaryExpression(firstToken, tmp, weakToken, ex2);
					tmp = be;
				}

				isWeakop = true;
			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {
			if (isWeakop) {
				return tmp;
			} else {
				return ex1;
			}

		} else {
			return null;
		}
	}

	private Expression Elem() {
		boolean isStrongop = false;
		Token firstToken = this.t;
		// Expression toReturn = null;
		Expression ex1 = null;
		Expression ex2 = null;
		BinaryExpression tmp = null;

		try {
			ex1 = Thing();
			while (this.isKind(STRONG_OPS)) {
				Token strongToken = this.t;
				match(STRONG_OPS);
				ex2 = Thing();

				if (!isStrongop) {
					tmp = new BinaryExpression(firstToken, ex1, strongToken,
							ex2);
				} else {
					BinaryExpression be = null;
					be = new BinaryExpression(firstToken, tmp, strongToken, ex2);
					tmp = be;
				}

				isStrongop = true;
			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {
			if (isStrongop) {
				return tmp;
			} else {
				return ex1;
			}

		} else {
			return null;
		}
	}

	private Expression Thing() {
		boolean isVstrongop = false;
		Token firstToken = this.t;
		// Expression toReturn = null;
		Expression ex1 = null;
		Expression ex2 = null;
		BinaryExpression tmp = null;

		try {
			ex1 = Factor();
			while (this.isKind(VERY_STRONG_OPS)) {
				Token vstrongToken = this.t;
				match(VERY_STRONG_OPS);
				ex2 = Factor();

				if (!isVstrongop) {
					tmp = new BinaryExpression(firstToken, ex1, vstrongToken,
							ex2);
				} else {
					BinaryExpression be = null;
					be = new BinaryExpression(firstToken, tmp, vstrongToken,
							ex2);
					tmp = be;
				}

				isVstrongop = true;
			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}

		if (exceptionList.isEmpty()) {
			if (isVstrongop) {
				return tmp;
			} else {
				return ex1;
			}

		} else {
			return null;
		}

	}

	private Expression Factor() {
		Token firstToken = this.t;
		Expression ex = null;
		try {
			if (this.isKind(INT_LIT)) {
				match(INT_LIT);
				ex = new IntLitExpression(firstToken, firstToken.getIntVal());
			} else if (this.isKind(BL_TRUE)) {
				match(BL_TRUE);
				ex = new BooleanLitExpression(firstToken,
						firstToken.getBooleanVal());
			} else if (this.isKind(BL_FALSE)) {
				match(BL_FALSE);
				ex = new BooleanLitExpression(firstToken,
						firstToken.getBooleanVal());
			} else if (this.isKind(STRING_LIT)) {
				match(STRING_LIT);
				ex = new StringLitExpression(firstToken, firstToken.getText());
			} else if (this.isKind(NOT)) {
				Expression exp = null;
				match(NOT);
				exp = Factor();
				ex = new UnaryExpression(firstToken, firstToken, exp);
			} else if (this.isKind(MINUS)) {
				Expression exp = null;
				match(MINUS);
				exp = Factor();
				ex = new UnaryExpression(firstToken, firstToken, exp);
			} else if (this.isKind(KW_SIZE)) {
				Expression exp = null;
				match(KW_SIZE);
				match(LPAREN);
				exp = Expression();
				match(RPAREN);
				ex = new SizeExpression(firstToken, exp);
			} else if (this.isKind(KW_KEY)) {
				Expression exp = null;
				match(KW_KEY);
				match(LPAREN);
				exp = Expression();
				match(RPAREN);
				ex = new KeyExpression(firstToken, exp);
			} else if (this.isKind(KW_VALUE)) {
				Expression exp = null;
				match(KW_VALUE);
				match(LPAREN);
				exp = Expression();
				match(RPAREN);
				ex = new ValueExpression(firstToken, exp);
			} else if (this.isKind(LCURLY)) {
				Closure c = null;
				c = Closure();
				ex = new ClosureExpression(firstToken, c);
			} else if (this.isKind(IDENT)) {
				Expression exp = null;
				match(IDENT);
				if (this.isKind(LSQUARE)) {
					match(LSQUARE);
					exp = Expression();
					match(RSQUARE);
					ex = new ListOrMapElemExpression(firstToken, firstToken,
							exp);
				} else if (this.isKind(LPAREN)) {
					ex = ClosureEvalExpression(firstToken);
				} else {
					// just ident
					ex = new IdentExpression(firstToken, firstToken);
				}
			} else if (this.isKind(LPAREN)) {
				match(LPAREN);
				ex = Expression();
				match(RPAREN);
			} else {
				match(AT);
				if (this.isKind(AT)) {
					ex = MapList(firstToken);
				} else {
					ex = List(firstToken);
				}
			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}

		if (exceptionList.isEmpty()) {
			return ex;

		} else {
			return null;
		}

	}

	private LValue Lvalue() {
		LValue lv = null;
		Token firstToken = this.t;
		Expression ex = null;
		boolean isIdent = true;
		try {
			match(IDENT);
			if (this.isKind(LSQUARE)) {
				isIdent = false;
				match(LSQUARE);
				ex = Expression();
				match(RSQUARE);
			}
			if (isIdent) {
				lv = new IdentLValue(firstToken, firstToken);
			} else {
				lv = new ExpressionLValue(firstToken, firstToken, ex);
			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {

			return lv;
		} else {
			return null;
		}
	}

	private Expression ClosureEvalExpression(Token firstToken) {
		Expression ex = null;
		List<Expression> expressionList = null;
		// match(IDENT);

		try {
			match(LPAREN);
			expressionList = ExpressionList();
			match(RPAREN);
			ex = new ClosureEvalExpression(firstToken, firstToken,
					expressionList);
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {

			return ex;
		} else {
			return null;
		}
		// ClosureEvalExpression(Token firstToken, Token
		// identToken,List<Expression> expressionList) ;

	}

	private Expression List(Token firstToken) throws SyntaxException {
		// match(AT);
		List<Expression> exl = new ArrayList<Expression>();
		Expression ex = null;
		try {
			match(LSQUARE);
			exl = ExpressionList();
			match(RSQUARE);
			ex = new ListExpression(firstToken, exl);
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {

			return ex;
		} else {
			return null;
		}

	}

	private List<Expression> ExpressionList() {
		List<Expression> exl = new ArrayList<Expression>();
		Expression ex = null;
		try {
			if (this.isKind(RPAREN) || this.isKind(RSQUARE)) {
				// dont match because the list is empty
				return exl;
			} else {
				ex = Expression();
				exl.add(ex);
				while (this.isKind(COMMA)) {
					match(COMMA);
					ex = Expression();
					exl.add(ex);
				}
			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {

			return exl;
		} else {
			return null;
		}
	}

	private KeyValueExpression KeyValueExpression() {
		Token firstToken = this.t;
		KeyValueExpression ex = null;
		Expression key = null;
		Expression value = null;
		try {
			key = Expression();
			match(COLON);
			value = Expression();
			ex = new KeyValueExpression(firstToken, key, value);
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {

			return ex;
		} else {
			return null;
		}
	}

	private List<KeyValueExpression> KeyValueList() {
		List<KeyValueExpression> kvl = new ArrayList<KeyValueExpression>();
		KeyValueExpression kve = null;
		try {
			if (this.isKind(RSQUARE)) {
				// dont match
				return kvl;
			} else {
				kve = KeyValueExpression();
				kvl.add(kve);
				while (this.isKind(COMMA)) {
					match(COMMA);
					kve = KeyValueExpression();
					kvl.add(kve);
				}
			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {

			return kvl;
		} else {
			return null;
		}
	}

	private MapListExpression MapList(Token firstToken) {
		// match(AT);
		MapListExpression ex = null;
		List<KeyValueExpression> mapList = null;
		try {
			match(AT);
			match(LSQUARE);
			mapList = KeyValueList();
			match(RSQUARE);
			ex = new MapListExpression(firstToken, mapList);
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {

			return ex;
		} else {
			return null;
		}
	}

	private Declaration Declaration() {
		// Token firstToken = this.t;

		Declaration d = null;
		try {
			match(KW_DEF);
			Token ident = this.t;
			match(IDENT);
			try { // change this part to if statements and test in simple parser
				d = ClosureDec(ident);
			} catch (Exception e) {
				d = VarDec(ident);

			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}

		return d;
	}

	private ClosureDec ClosureDec(Token ident) throws SyntaxException {
		// ClosureDec(Token firstToken, Token identToken, Closure closure)
		Closure c = null;
		// try {
		// match(IDENT);
		match(ASSIGN);
		c = Closure();
		// } catch (SyntaxException e) {
		// exceptionList.add(e);
		// return null;
		// }
		if (exceptionList.isEmpty()) {
			ClosureDec d = new ClosureDec(ident, ident, c);
			return d;
		} else {
			return null;
		}
	}

	private Closure Closure() {
		List<VarDec> fargs = null;
		List<Statement> statementList = new ArrayList<Statement>();
		Statement s = null;
		Token firstToken = this.t;
		try {
			match(LCURLY);
			fargs = FormalArgList();
			match(ARROW);
			while (!this.isKind(RCURLY)) {
				s = Statement(); // check if it is null and only add if it is
									// not
				if (s != null) {
					statementList.add(s);
				}

				match(SEMICOLON);
			}

			match(RCURLY);
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {
			Closure c = new Closure(firstToken, fargs, statementList);
			return c;
		} else {
			return null;
		}

	}

	private List<VarDec> FormalArgList() {
		List<VarDec> fargs = new ArrayList<VarDec>();
		VarDec v = null;
		if (this.isKind(ARROW)) {
			return fargs;
		} else {
			try {
				Token ident = this.t;
				match(IDENT); // test
				v = VarDec(ident);
				fargs.add(v);
				while (this.isKind(COMMA)) {
					match(COMMA);
					ident = this.t;
					match(IDENT);
					v = VarDec(ident);
					fargs.add(v);
				}
			} catch (SyntaxException e) {
				exceptionList.add(e);
			}
		}
		if (exceptionList.isEmpty()) {
			return fargs;
		} else {
			return null;
		}

	}

	private VarDec VarDec(Token ident) {
		// VarDec(Token firstToken, Token identToken, Type type)
		Token firstToken = this.t;
		VarDec v = null;
		Type t = null;
		try {
			// match(IDENT);
			if (this.isKind(COLON)) { // maybe remove this if but not its
										// contents
				match(COLON);
				t = Type();
			} else {
				t = new UndeclaredType(ident);
			}
			v = new VarDec(ident, ident, t); // t will be null if there is
												// no colon
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		return v;

	}

	private Type Type() {
		Type t = null;
		try {
			if (this.isKind(KW_INT) || this.isKind(KW_BOOLEAN)
					|| this.isKind(KW_STRING)) {
				t = SimpleType();
			} else {
				Token firstToken = this.t;
				match(AT);
				if (this.isKind(AT)) {
					t = KeyValueType(firstToken);
				} else {
					t = ListType(firstToken);
				}
			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		return t;

	}

	private ListType ListType(Token firstToken) {
		// ListType(Token firstToken, Type type)
		Type t = null;
		try {
			match(LSQUARE);
			t = Type();
			match(RSQUARE);
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {
			ListType k = new ListType(firstToken, t);
			return k;
		} else {
			return null;
		}

	}

	private KeyValueType KeyValueType(Token firstToken) {
		// KeyValueType(Token firstToken, SimpleType keyType, Type valueType)
		SimpleType s = null;
		Type t = null;
		try {
			match(AT);
			match(LSQUARE);
			s = SimpleType();
			match(COLON);
			t = Type();
			match(RSQUARE);
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}

		if (exceptionList.isEmpty()) {
			KeyValueType k = new KeyValueType(firstToken, s, t);
			return k;
		} else {
			return null;
		}

	}

	private SimpleType SimpleType() {
		Token firstToken = this.t;
		SimpleType s = new SimpleType(firstToken, firstToken);
		try {
			if (this.isKind(KW_INT)) {
				match(KW_INT);
			} else if (this.isKind(KW_BOOLEAN)) {
				match(KW_BOOLEAN);
			} else {
				match(KW_STRING);
			}
		} catch (SyntaxException e) {
			exceptionList.add(e);
		}
		if (exceptionList.isEmpty()) {

			return s;
		} else {
			return null;
		}

	}

	public List<SyntaxException> getExceptionList() {

		return this.exceptionList;

	}
	
	public List<SyntaxException> getErrors() {

		return this.exceptionList;

	}

}