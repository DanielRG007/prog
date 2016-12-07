# -*- coding: utf-8 -*-
# Generated by Django 1.9 on 2016-12-07 05:58
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Line',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('p_origen', models.CharField(default=b'(0.0,0.0)', max_length=50)),
                ('p_destino', models.CharField(default=b'(0.0,0.0)', max_length=50)),
                ('camino_mas_corto', models.CharField(default=b'', max_length=250)),
                ('tipo_transporte', models.CharField(default=1, max_length=50))
            ],
            options={
                'verbose_name': 'Line',
                'verbose_name_plural': 'Lines',
            },
        ),
        migrations.CreateModel(
            name='Match',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
            ],
            options={
                'verbose_name': 'Match',
                'verbose_name_plural': 'Matches',
            },
        ),
        migrations.CreateModel(
            name='Point',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('lon', models.FloatField(default=1.0)),
                ('lat', models.FloatField(default=0.0)),
            ],
            options={
                'verbose_name': 'Point',
                'verbose_name_plural': 'Points',
            },
        ),
    ]
