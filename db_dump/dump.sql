PGDMP          1                {         
   cleverbank    15.2    15.2 $               0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    16858 
   cleverbank    DATABASE     �   CREATE DATABASE cleverbank WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Belarusian_Belarus.1251';
    DROP DATABASE cleverbank;
                postgres    false            �            1259    17001    account    TABLE     �   CREATE TABLE public.account (
    id integer NOT NULL,
    currency character varying(16) NOT NULL,
    openingdate timestamp without time zone NOT NULL,
    balance double precision,
    bankid integer NOT NULL,
    appuserid integer NOT NULL
);
    DROP TABLE public.account;
       public         heap    postgres    false            �            1259    17000    account_id_seq    SEQUENCE     �   CREATE SEQUENCE public.account_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.account_id_seq;
       public          postgres    false    219                        0    0    account_id_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE public.account_id_seq OWNED BY public.account.id;
          public          postgres    false    218            �            1259    16868    appuser    TABLE     ^   CREATE TABLE public.appuser (
    id integer NOT NULL,
    fullname character varying(128)
);
    DROP TABLE public.appuser;
       public         heap    postgres    false            �            1259    16867    appuser_id_seq    SEQUENCE     �   CREATE SEQUENCE public.appuser_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.appuser_id_seq;
       public          postgres    false    217            !           0    0    appuser_id_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE public.appuser_id_seq OWNED BY public.appuser.id;
          public          postgres    false    216            �            1259    16861    bank    TABLE     W   CREATE TABLE public.bank (
    id integer NOT NULL,
    name character varying(128)
);
    DROP TABLE public.bank;
       public         heap    postgres    false            �            1259    16860    bank_id_seq    SEQUENCE     �   CREATE SEQUENCE public.bank_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 "   DROP SEQUENCE public.bank_id_seq;
       public          postgres    false    215            "           0    0    bank_id_seq    SEQUENCE OWNED BY     ;   ALTER SEQUENCE public.bank_id_seq OWNED BY public.bank.id;
          public          postgres    false    214            �            1259    17052    transaction    TABLE     �   CREATE TABLE public.transaction (
    id integer NOT NULL,
    date timestamp without time zone NOT NULL,
    type character varying(32) NOT NULL,
    receiveraccid integer,
    senderaccid integer,
    total double precision NOT NULL
);
    DROP TABLE public.transaction;
       public         heap    postgres    false            �            1259    17051    transaction_id_seq    SEQUENCE     �   CREATE SEQUENCE public.transaction_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.transaction_id_seq;
       public          postgres    false    221            #           0    0    transaction_id_seq    SEQUENCE OWNED BY     I   ALTER SEQUENCE public.transaction_id_seq OWNED BY public.transaction.id;
          public          postgres    false    220            v           2604    17004 
   account id    DEFAULT     h   ALTER TABLE ONLY public.account ALTER COLUMN id SET DEFAULT nextval('public.account_id_seq'::regclass);
 9   ALTER TABLE public.account ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    218    219    219            u           2604    16871 
   appuser id    DEFAULT     h   ALTER TABLE ONLY public.appuser ALTER COLUMN id SET DEFAULT nextval('public.appuser_id_seq'::regclass);
 9   ALTER TABLE public.appuser ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    217    216    217            t           2604    16864    bank id    DEFAULT     b   ALTER TABLE ONLY public.bank ALTER COLUMN id SET DEFAULT nextval('public.bank_id_seq'::regclass);
 6   ALTER TABLE public.bank ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    214    215    215            w           2604    17055    transaction id    DEFAULT     p   ALTER TABLE ONLY public.transaction ALTER COLUMN id SET DEFAULT nextval('public.transaction_id_seq'::regclass);
 =   ALTER TABLE public.transaction ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    221    220    221                      0    17001    account 
   TABLE DATA           X   COPY public.account (id, currency, openingdate, balance, bankid, appuserid) FROM stdin;
    public          postgres    false    219   '                 0    16868    appuser 
   TABLE DATA           /   COPY public.appuser (id, fullname) FROM stdin;
    public          postgres    false    217   L)                 0    16861    bank 
   TABLE DATA           (   COPY public.bank (id, name) FROM stdin;
    public          postgres    false    215   �*                 0    17052    transaction 
   TABLE DATA           X   COPY public.transaction (id, date, type, receiveraccid, senderaccid, total) FROM stdin;
    public          postgres    false    221   +       $           0    0    account_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.account_id_seq', 45, true);
          public          postgres    false    218            %           0    0    appuser_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.appuser_id_seq', 22, true);
          public          postgres    false    216            &           0    0    bank_id_seq    SEQUENCE SET     9   SELECT pg_catalog.setval('public.bank_id_seq', 7, true);
          public          postgres    false    214            '           0    0    transaction_id_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('public.transaction_id_seq', 38, true);
          public          postgres    false    220            }           2606    17006    account account_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.account DROP CONSTRAINT account_pkey;
       public            postgres    false    219            {           2606    16873    appuser appuser_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.appuser
    ADD CONSTRAINT appuser_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.appuser DROP CONSTRAINT appuser_pkey;
       public            postgres    false    217            y           2606    16866    bank bank_pkey 
   CONSTRAINT     L   ALTER TABLE ONLY public.bank
    ADD CONSTRAINT bank_pkey PRIMARY KEY (id);
 8   ALTER TABLE ONLY public.bank DROP CONSTRAINT bank_pkey;
       public            postgres    false    215                       2606    17057    transaction transaction_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT transaction_pkey PRIMARY KEY (id);
 F   ALTER TABLE ONLY public.transaction DROP CONSTRAINT transaction_pkey;
       public            postgres    false    221            �           2606    17012    account account_appuserid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_appuserid_fkey FOREIGN KEY (appuserid) REFERENCES public.appuser(id);
 H   ALTER TABLE ONLY public.account DROP CONSTRAINT account_appuserid_fkey;
       public          postgres    false    219    217    3195            �           2606    17007    account account_bankid_fkey    FK CONSTRAINT     x   ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_bankid_fkey FOREIGN KEY (bankid) REFERENCES public.bank(id);
 E   ALTER TABLE ONLY public.account DROP CONSTRAINT account_bankid_fkey;
       public          postgres    false    3193    215    219            �           2606    17058 *   transaction transaction_receiveraccid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT transaction_receiveraccid_fkey FOREIGN KEY (receiveraccid) REFERENCES public.account(id);
 T   ALTER TABLE ONLY public.transaction DROP CONSTRAINT transaction_receiveraccid_fkey;
       public          postgres    false    219    221    3197            �           2606    17063 (   transaction transaction_senderaccid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT transaction_senderaccid_fkey FOREIGN KEY (senderaccid) REFERENCES public.account(id);
 R   ALTER TABLE ONLY public.transaction DROP CONSTRAINT transaction_senderaccid_fkey;
       public          postgres    false    221    3197    219               "  x�UT;�1�G��&�>��A��"�r�s�Ҏ=@�f�"-��������=�e�oYO|�����@6 �j�� ����O@& �"���z�_��e�-@� �.�]� ٨6H��ѩ� �ӺxҝģV�7T�h���Q ̌j�4	/byfM�YP��N��hm�Ч+lIى����O�X,93�Y��)�]Iқx=�NusWy�d��fn�w����!�9�G��MiH���� �p���k�o$#�$�4��m[ (���a�4���r0g&�g�)a�4���h&	�g�%�t�E�YH�J8wK}.�'�5�H�'M��=�+�*�$��&�4�j�(�%�����ԍd������m��=@癷5שּ����'�5O���kO�@q�OͫkZ3I����i^K1т<PZz�^#�Ӄ���{,�e�����,L�7i�����+t�U�D:�K"��?���V�D��K&�O�&��ee`��L��l�T��\0�cN�����X�e'n/�r����Ke������         j  x�}R[N�P��]EW`,�����	�HbQ�ߤ�4������9�mJ�ү�s:ss&0xA�;�z`���r$��M��P����2Xb���,���6x��H�N�CQ`N�`3l�������C��?�o�k0E��s;&��%�3h�^IǐA^��{&X���M��#|��1H+�'?[Uׅ�SI;b�P�>e���99�w�0����,t�؉.�ˁV�\Nh`(E!@"��������������S�yHi`'|��7��q�P$б�}:�#���2v2�G>����c;9ӁW��J�gE�t����XJ.��7��N�Vߡ�x�����j	uKjՖ��|�O>�0�m����6.����y��H         6   x�3�t�I-K-rJ���2�t�)�H��9�����LN'?(Ӕӷ���� >�F         F  x�u�Mj�0���)��?��]`&�x�f����M?����vWUS�Ze�����u��q{z���|K�8���.��.HI�L�p� m	U� -�d�̊d�-!�����6|^��K�7z{~y}{K#�{O�֊�`_B��@��Ԧ�er,��g�&YG�����n?�����Gp�%TǙ��K����T���׏?�oO���E3&*t���L&Q�D��2?`c�L0��)�0���ɵVXE�����S���,� �v$k��/�j`,��C�a�ш�_��{I�g
˸F�_��K	�m�1Eh\�aT=r�T�#\�h|z$�x�p�%"�t���<J�㭌������g��������}Ǻ��`=�3�6�4�m/$d�-�j�2��W����b�E7���b��p(��=�g;�pF�c$�� ���v8%���l:(u�i;\�U�g�����֊N�4Qx��/�p�U�*��#<�u	��m����Jg�8�e����[�-� '��+zX!�Zm�\ԇ�ߩS��=�V���O���}%)H]��(1���s��1?2     