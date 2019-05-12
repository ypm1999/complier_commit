// Generated from /home/sjtu-ypm/complier/src/src/com/mxcomplier/LaxerParser/MxStar.g4 by ANTLR 4.7.2
package com.mxcomplier.LaxerParser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MxStarLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            Null = 1, True = 2, False = 3, This = 4, Void = 5, Int = 6, String = 7, Bool = 8, New = 9,
            Class = 10, Return = 11, Continue = 12, Break = 13, Else = 14, For = 15, While = 16,
            If = 17, LeftParen = 18, RightParen = 19, LeftBracket = 20, RightBracket = 21, LeftBrace = 22,
            RightBrace = 23, Less = 24, LessEqual = 25, Greater = 26, GreaterEqual = 27, LeftShift = 28,
            RightShift = 29, Plus = 30, PlusPlus = 31, Minus = 32, MinusMinus = 33, Star = 34,
            Div = 35, Mod = 36, And = 37, Or = 38, AndAnd = 39, OrOr = 40, Caret = 41, Not = 42, Tilde = 43,
            Question = 44, Colon = 45, Semi = 46, Comma = 47, Assign = 48, Equal = 49, NotEqual = 50,
            Dot = 51, Identifier = 52, IntegerConstant = 53, Nondigit = 54, Digit = 55, NonzeroDigit = 56,
            CharacterConstant = 57, Whitespace = 58, Newline = 59, BlockComment = 60, LineComment = 61;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "Null", "True", "False", "This", "Void", "Int", "String", "Bool", "New",
                "Class", "Return", "Continue", "Break", "Else", "For", "While", "If",
                "LeftParen", "RightParen", "LeftBracket", "RightBracket", "LeftBrace",
                "RightBrace", "Less", "LessEqual", "Greater", "GreaterEqual", "LeftShift",
                "RightShift", "Plus", "PlusPlus", "Minus", "MinusMinus", "Star", "Div",
                "Mod", "And", "Or", "AndAnd", "OrOr", "Caret", "Not", "Tilde", "Question",
                "Colon", "Semi", "Comma", "Assign", "Equal", "NotEqual", "Dot", "Identifier",
                "IntegerConstant", "Nondigit", "Digit", "NonzeroDigit", "CharacterConstant",
                "CCharSequence", "CChar", "EscapeSequence", "Whitespace", "Newline",
                "BlockComment", "LineComment"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'null'", "'true'", "'false'", "'this'", "'void'", "'int'", "'string'",
                "'bool'", "'new'", "'class'", "'return'", "'continue'", "'break'", "'else'",
                "'for'", "'while'", "'if'", "'('", "')'", "'['", "']'", "'{'", "'}'",
                "'<'", "'<='", "'>'", "'>='", "'<<'", "'>>'", "'+'", "'++'", "'-'", "'--'",
                "'*'", "'/'", "'%'", "'&'", "'|'", "'&&'", "'||'", "'^'", "'!'", "'~'",
                "'?'", "':'", "';'", "','", "'='", "'=='", "'!='", "'.'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "Null", "True", "False", "This", "Void", "Int", "String", "Bool",
                "New", "Class", "Return", "Continue", "Break", "Else", "For", "While",
                "If", "LeftParen", "RightParen", "LeftBracket", "RightBracket", "LeftBrace",
                "RightBrace", "Less", "LessEqual", "Greater", "GreaterEqual", "LeftShift",
                "RightShift", "Plus", "PlusPlus", "Minus", "MinusMinus", "Star", "Div",
                "Mod", "And", "Or", "AndAnd", "OrOr", "Caret", "Not", "Tilde", "Question",
                "Colon", "Semi", "Comma", "Assign", "Equal", "NotEqual", "Dot", "Identifier",
                "IntegerConstant", "Nondigit", "Digit", "NonzeroDigit", "CharacterConstant",
                "Whitespace", "Newline", "BlockComment", "LineComment"
        };
    }

    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;

    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }

            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }


    public MxStarLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "MxStar.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2?\u0181\b\1\4\2\t" +
                    "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" +
                    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
                    "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4" +
                    ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t" +
                    "\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t=" +
                    "\4>\t>\4?\t?\4@\t@\4A\tA\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3" +
                    "\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7" +
                    "\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3" +
                    "\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3" +
                    "\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3" +
                    "\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3" +
                    "\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3" +
                    "\31\3\32\3\32\3\32\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3" +
                    "\36\3\37\3\37\3 \3 \3 \3!\3!\3\"\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3" +
                    "\'\3(\3(\3(\3)\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61" +
                    "\3\61\3\62\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\65\3\65\3\65\3\65\7\65" +
                    "\u0132\n\65\f\65\16\65\u0135\13\65\3\66\3\66\3\66\7\66\u013a\n\66\f\66" +
                    "\16\66\u013d\13\66\5\66\u013f\n\66\3\67\3\67\38\38\39\39\3:\3:\5:\u0149" +
                    "\n:\3:\3:\3;\6;\u014e\n;\r;\16;\u014f\3<\3<\5<\u0154\n<\3=\3=\3=\3>\6" +
                    ">\u015a\n>\r>\16>\u015b\3>\3>\3?\3?\5?\u0162\n?\3?\5?\u0165\n?\3?\3?\3" +
                    "@\3@\3@\3@\7@\u016d\n@\f@\16@\u0170\13@\3@\3@\3@\3@\3@\3A\3A\3A\3A\7A" +
                    "\u017b\nA\fA\16A\u017e\13A\3A\3A\3\u016e\2B\3\3\5\4\7\5\t\6\13\7\r\b\17" +
                    "\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+" +
                    "\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+" +
                    "U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u\2w\2y\2{<}=\177>\u0081" +
                    "?\3\2\t\4\2C\\c|\3\2\62;\3\2\63;\6\2\f\f\17\17$$^^\7\2$$^^ppttvv\4\2\13" +
                    "\13\"\"\4\2\f\f\17\17\2\u018a\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t" +
                    "\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2" +
                    "\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2" +
                    "\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2" +
                    "+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2" +
                    "\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2" +
                    "C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3" +
                    "\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2" +
                    "\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2" +
                    "i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2{\3" +
                    "\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\3\u0083\3\2\2\2\5\u0088" +
                    "\3\2\2\2\7\u008d\3\2\2\2\t\u0093\3\2\2\2\13\u0098\3\2\2\2\r\u009d\3\2" +
                    "\2\2\17\u00a1\3\2\2\2\21\u00a8\3\2\2\2\23\u00ad\3\2\2\2\25\u00b1\3\2\2" +
                    "\2\27\u00b7\3\2\2\2\31\u00be\3\2\2\2\33\u00c7\3\2\2\2\35\u00cd\3\2\2\2" +
                    "\37\u00d2\3\2\2\2!\u00d6\3\2\2\2#\u00dc\3\2\2\2%\u00df\3\2\2\2\'\u00e1" +
                    "\3\2\2\2)\u00e3\3\2\2\2+\u00e5\3\2\2\2-\u00e7\3\2\2\2/\u00e9\3\2\2\2\61" +
                    "\u00eb\3\2\2\2\63\u00ed\3\2\2\2\65\u00f0\3\2\2\2\67\u00f2\3\2\2\29\u00f5" +
                    "\3\2\2\2;\u00f8\3\2\2\2=\u00fb\3\2\2\2?\u00fd\3\2\2\2A\u0100\3\2\2\2C" +
                    "\u0102\3\2\2\2E\u0105\3\2\2\2G\u0107\3\2\2\2I\u0109\3\2\2\2K\u010b\3\2" +
                    "\2\2M\u010d\3\2\2\2O\u010f\3\2\2\2Q\u0112\3\2\2\2S\u0115\3\2\2\2U\u0117" +
                    "\3\2\2\2W\u0119\3\2\2\2Y\u011b\3\2\2\2[\u011d\3\2\2\2]\u011f\3\2\2\2_" +
                    "\u0121\3\2\2\2a\u0123\3\2\2\2c\u0125\3\2\2\2e\u0128\3\2\2\2g\u012b\3\2" +
                    "\2\2i\u012d\3\2\2\2k\u013e\3\2\2\2m\u0140\3\2\2\2o\u0142\3\2\2\2q\u0144" +
                    "\3\2\2\2s\u0146\3\2\2\2u\u014d\3\2\2\2w\u0153\3\2\2\2y\u0155\3\2\2\2{" +
                    "\u0159\3\2\2\2}\u0164\3\2\2\2\177\u0168\3\2\2\2\u0081\u0176\3\2\2\2\u0083" +
                    "\u0084\7p\2\2\u0084\u0085\7w\2\2\u0085\u0086\7n\2\2\u0086\u0087\7n\2\2" +
                    "\u0087\4\3\2\2\2\u0088\u0089\7v\2\2\u0089\u008a\7t\2\2\u008a\u008b\7w" +
                    "\2\2\u008b\u008c\7g\2\2\u008c\6\3\2\2\2\u008d\u008e\7h\2\2\u008e\u008f" +
                    "\7c\2\2\u008f\u0090\7n\2\2\u0090\u0091\7u\2\2\u0091\u0092\7g\2\2\u0092" +
                    "\b\3\2\2\2\u0093\u0094\7v\2\2\u0094\u0095\7j\2\2\u0095\u0096\7k\2\2\u0096" +
                    "\u0097\7u\2\2\u0097\n\3\2\2\2\u0098\u0099\7x\2\2\u0099\u009a\7q\2\2\u009a" +
                    "\u009b\7k\2\2\u009b\u009c\7f\2\2\u009c\f\3\2\2\2\u009d\u009e\7k\2\2\u009e" +
                    "\u009f\7p\2\2\u009f\u00a0\7v\2\2\u00a0\16\3\2\2\2\u00a1\u00a2\7u\2\2\u00a2" +
                    "\u00a3\7v\2\2\u00a3\u00a4\7t\2\2\u00a4\u00a5\7k\2\2\u00a5\u00a6\7p\2\2" +
                    "\u00a6\u00a7\7i\2\2\u00a7\20\3\2\2\2\u00a8\u00a9\7d\2\2\u00a9\u00aa\7" +
                    "q\2\2\u00aa\u00ab\7q\2\2\u00ab\u00ac\7n\2\2\u00ac\22\3\2\2\2\u00ad\u00ae" +
                    "\7p\2\2\u00ae\u00af\7g\2\2\u00af\u00b0\7y\2\2\u00b0\24\3\2\2\2\u00b1\u00b2" +
                    "\7e\2\2\u00b2\u00b3\7n\2\2\u00b3\u00b4\7c\2\2\u00b4\u00b5\7u\2\2\u00b5" +
                    "\u00b6\7u\2\2\u00b6\26\3\2\2\2\u00b7\u00b8\7t\2\2\u00b8\u00b9\7g\2\2\u00b9" +
                    "\u00ba\7v\2\2\u00ba\u00bb\7w\2\2\u00bb\u00bc\7t\2\2\u00bc\u00bd\7p\2\2" +
                    "\u00bd\30\3\2\2\2\u00be\u00bf\7e\2\2\u00bf\u00c0\7q\2\2\u00c0\u00c1\7" +
                    "p\2\2\u00c1\u00c2\7v\2\2\u00c2\u00c3\7k\2\2\u00c3\u00c4\7p\2\2\u00c4\u00c5" +
                    "\7w\2\2\u00c5\u00c6\7g\2\2\u00c6\32\3\2\2\2\u00c7\u00c8\7d\2\2\u00c8\u00c9" +
                    "\7t\2\2\u00c9\u00ca\7g\2\2\u00ca\u00cb\7c\2\2\u00cb\u00cc\7m\2\2\u00cc" +
                    "\34\3\2\2\2\u00cd\u00ce\7g\2\2\u00ce\u00cf\7n\2\2\u00cf\u00d0\7u\2\2\u00d0" +
                    "\u00d1\7g\2\2\u00d1\36\3\2\2\2\u00d2\u00d3\7h\2\2\u00d3\u00d4\7q\2\2\u00d4" +
                    "\u00d5\7t\2\2\u00d5 \3\2\2\2\u00d6\u00d7\7y\2\2\u00d7\u00d8\7j\2\2\u00d8" +
                    "\u00d9\7k\2\2\u00d9\u00da\7n\2\2\u00da\u00db\7g\2\2\u00db\"\3\2\2\2\u00dc" +
                    "\u00dd\7k\2\2\u00dd\u00de\7h\2\2\u00de$\3\2\2\2\u00df\u00e0\7*\2\2\u00e0" +
                    "&\3\2\2\2\u00e1\u00e2\7+\2\2\u00e2(\3\2\2\2\u00e3\u00e4\7]\2\2\u00e4*" +
                    "\3\2\2\2\u00e5\u00e6\7_\2\2\u00e6,\3\2\2\2\u00e7\u00e8\7}\2\2\u00e8.\3" +
                    "\2\2\2\u00e9\u00ea\7\177\2\2\u00ea\60\3\2\2\2\u00eb\u00ec\7>\2\2\u00ec" +
                    "\62\3\2\2\2\u00ed\u00ee\7>\2\2\u00ee\u00ef\7?\2\2\u00ef\64\3\2\2\2\u00f0" +
                    "\u00f1\7@\2\2\u00f1\66\3\2\2\2\u00f2\u00f3\7@\2\2\u00f3\u00f4\7?\2\2\u00f4" +
                    "8\3\2\2\2\u00f5\u00f6\7>\2\2\u00f6\u00f7\7>\2\2\u00f7:\3\2\2\2\u00f8\u00f9" +
                    "\7@\2\2\u00f9\u00fa\7@\2\2\u00fa<\3\2\2\2\u00fb\u00fc\7-\2\2\u00fc>\3" +
                    "\2\2\2\u00fd\u00fe\7-\2\2\u00fe\u00ff\7-\2\2\u00ff@\3\2\2\2\u0100\u0101" +
                    "\7/\2\2\u0101B\3\2\2\2\u0102\u0103\7/\2\2\u0103\u0104\7/\2\2\u0104D\3" +
                    "\2\2\2\u0105\u0106\7,\2\2\u0106F\3\2\2\2\u0107\u0108\7\61\2\2\u0108H\3" +
                    "\2\2\2\u0109\u010a\7\'\2\2\u010aJ\3\2\2\2\u010b\u010c\7(\2\2\u010cL\3" +
                    "\2\2\2\u010d\u010e\7~\2\2\u010eN\3\2\2\2\u010f\u0110\7(\2\2\u0110\u0111" +
                    "\7(\2\2\u0111P\3\2\2\2\u0112\u0113\7~\2\2\u0113\u0114\7~\2\2\u0114R\3" +
                    "\2\2\2\u0115\u0116\7`\2\2\u0116T\3\2\2\2\u0117\u0118\7#\2\2\u0118V\3\2" +
                    "\2\2\u0119\u011a\7\u0080\2\2\u011aX\3\2\2\2\u011b\u011c\7A\2\2\u011cZ" +
                    "\3\2\2\2\u011d\u011e\7<\2\2\u011e\\\3\2\2\2\u011f\u0120\7=\2\2\u0120^" +
                    "\3\2\2\2\u0121\u0122\7.\2\2\u0122`\3\2\2\2\u0123\u0124\7?\2\2\u0124b\3" +
                    "\2\2\2\u0125\u0126\7?\2\2\u0126\u0127\7?\2\2\u0127d\3\2\2\2\u0128\u0129" +
                    "\7#\2\2\u0129\u012a\7?\2\2\u012af\3\2\2\2\u012b\u012c\7\60\2\2\u012ch" +
                    "\3\2\2\2\u012d\u0133\5m\67\2\u012e\u0132\5m\67\2\u012f\u0132\5o8\2\u0130" +
                    "\u0132\7a\2\2\u0131\u012e\3\2\2\2\u0131\u012f\3\2\2\2\u0131\u0130\3\2" +
                    "\2\2\u0132\u0135\3\2\2\2\u0133\u0131\3\2\2\2\u0133\u0134\3\2\2\2\u0134" +
                    "j\3\2\2\2\u0135\u0133\3\2\2\2\u0136\u013f\7\62\2\2\u0137\u013b\5q9\2\u0138" +
                    "\u013a\5o8\2\u0139\u0138\3\2\2\2\u013a\u013d\3\2\2\2\u013b\u0139\3\2\2" +
                    "\2\u013b\u013c\3\2\2\2\u013c\u013f\3\2\2\2\u013d\u013b\3\2\2\2\u013e\u0136" +
                    "\3\2\2\2\u013e\u0137\3\2\2\2\u013fl\3\2\2\2\u0140\u0141\t\2\2\2\u0141" +
                    "n\3\2\2\2\u0142\u0143\t\3\2\2\u0143p\3\2\2\2\u0144\u0145\t\4\2\2\u0145" +
                    "r\3\2\2\2\u0146\u0148\7$\2\2\u0147\u0149\5u;\2\u0148\u0147\3\2\2\2\u0148" +
                    "\u0149\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014b\7$\2\2\u014bt\3\2\2\2\u014c" +
                    "\u014e\5w<\2\u014d\u014c\3\2\2\2\u014e\u014f\3\2\2\2\u014f\u014d\3\2\2" +
                    "\2\u014f\u0150\3\2\2\2\u0150v\3\2\2\2\u0151\u0154\5y=\2\u0152\u0154\n" +
                    "\5\2\2\u0153\u0151\3\2\2\2\u0153\u0152\3\2\2\2\u0154x\3\2\2\2\u0155\u0156" +
                    "\7^\2\2\u0156\u0157\t\6\2\2\u0157z\3\2\2\2\u0158\u015a\t\7\2\2\u0159\u0158" +
                    "\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c" +
                    "\u015d\3\2\2\2\u015d\u015e\b>\2\2\u015e|\3\2\2\2\u015f\u0161\7\17\2\2" +
                    "\u0160\u0162\7\f\2\2\u0161\u0160\3\2\2\2\u0161\u0162\3\2\2\2\u0162\u0165" +
                    "\3\2\2\2\u0163\u0165\7\f\2\2\u0164\u015f\3\2\2\2\u0164\u0163\3\2\2\2\u0165" +
                    "\u0166\3\2\2\2\u0166\u0167\b?\2\2\u0167~\3\2\2\2\u0168\u0169\7\61\2\2" +
                    "\u0169\u016a\7,\2\2\u016a\u016e\3\2\2\2\u016b\u016d\13\2\2\2\u016c\u016b" +
                    "\3\2\2\2\u016d\u0170\3\2\2\2\u016e\u016f\3\2\2\2\u016e\u016c\3\2\2\2\u016f" +
                    "\u0171\3\2\2\2\u0170\u016e\3\2\2\2\u0171\u0172\7,\2\2\u0172\u0173\7\61" +
                    "\2\2\u0173\u0174\3\2\2\2\u0174\u0175\b@\2\2\u0175\u0080\3\2\2\2\u0176" +
                    "\u0177\7\61\2\2\u0177\u0178\7\61\2\2\u0178\u017c\3\2\2\2\u0179\u017b\n" +
                    "\b\2\2\u017a\u0179\3\2\2\2\u017b\u017e\3\2\2\2\u017c\u017a\3\2\2\2\u017c" +
                    "\u017d\3\2\2\2\u017d\u017f\3\2\2\2\u017e\u017c\3\2\2\2\u017f\u0180\bA" +
                    "\2\2\u0180\u0082\3\2\2\2\17\2\u0131\u0133\u013b\u013e\u0148\u014f\u0153" +
                    "\u015b\u0161\u0164\u016e\u017c\3\b\2\2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}